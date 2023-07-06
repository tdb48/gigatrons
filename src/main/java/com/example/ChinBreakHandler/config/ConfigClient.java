package com.example.ChinBreakHandler.config;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.http.api.RuneLiteAPI;
import net.runelite.http.api.config.ConfigPatch;
import net.runelite.http.api.config.ConfigPatchResult;
import net.runelite.http.api.config.Configuration;
import net.runelite.http.api.config.Profile;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Slf4j
public class ConfigClient
{
    private final OkHttpClient client;
    private final HttpUrl apiBase;
    private final Gson gson;

    @Setter
    private UUID uuid;

    @Inject
    private ConfigClient(OkHttpClient client, @Named("runelite.api.base") HttpUrl apiBase, Gson gson)
    {
        this.client = client;
        this.apiBase = apiBase;
        this.gson = gson;
    }

    public List<Profile> profiles() throws IOException
    {
        HttpUrl url = apiBase.newBuilder()
                .addPathSegment("config")
                .addPathSegment("v3")
                .addPathSegment("list")
                .build();

        log.debug("Built URI: {}", url);

        Request request = new Request.Builder()
                .header(RuneLiteAPI.RUNELITE_AUTH, uuid.toString())
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute())
        {
            if (!response.isSuccessful())
            {
                log.error("non-successful response loading profiles: {}", response.code());
                return null;
            }

            InputStream in = response.body().byteStream();
            // CHECKSTYLE:OFF
            final Type type = new TypeToken<List<Profile>>() {}.getType();
            // CHECKSTYLE:ON
            return gson.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), type);
        }
        catch (JsonParseException ex)
        {
            throw new IOException(ex);
        }
    }

    public Configuration get(long profile) throws IOException
    {
        HttpUrl url = apiBase.newBuilder()
                .addPathSegment("config")
                .addPathSegment("v3")
                .addPathSegment(Long.toString(profile))
                .build();

        log.debug("Built URI: {}", url);

        Request request = new Request.Builder()
                .header(RuneLiteAPI.RUNELITE_AUTH, uuid.toString())
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute())
        {
            InputStream in = response.body().byteStream();
            return gson.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), Configuration.class);
        }
        catch (JsonParseException ex)
        {
            throw new IOException(ex);
        }
    }

    public CompletableFuture<ConfigPatchResult> patch(ConfigPatch patch, long profile)
    {
        HttpUrl url = apiBase.newBuilder()
                .addPathSegment("config")
                .addPathSegment("v3")
                .addPathSegment(Long.toString(profile))
                .build();

        log.debug("Built URI: {}", url);

        Request request = new Request.Builder()
                .patch(RequestBody.create(RuneLiteAPI.JSON, gson.toJson(patch)))
                .header(RuneLiteAPI.RUNELITE_AUTH, uuid.toString())
                .url(url)
                .build();

        CompletableFuture<ConfigPatchResult> future = new CompletableFuture<>();
        client.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                log.warn("Unable to synchronize configuration item", e);
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response)
            {
                try // NOPMD: UseTryWithResources
                {
                    if (response.code() != 200)
                    {
                        String body = "bad response";
                        try
                        {
                            body = response.body().string();
                        }
                        catch (IOException ignored)
                        {
                        }

                        log.warn("failed to synchronize some of {}/{} configuration values: {}",
                                patch.getEdit().size(), patch.getUnset().size(), body);
                        future.complete(null);
                    }
                    else
                    {
                        log.debug("Synchronized {}/{} configuration values",
                                patch.getEdit().size(), patch.getUnset().size());
                        future.complete(gson.fromJson(new InputStreamReader(response.body().byteStream(), StandardCharsets.UTF_8), ConfigPatchResult.class));
                    }
                }
                catch (Exception ex)
                {
                    future.completeExceptionally(ex);
                }
                finally
                {
                    response.close();
                }
            }
        });

        return future;
    }

    public void delete(long profile)
    {
        HttpUrl url = apiBase.newBuilder()
                .addPathSegment("config")
                .addPathSegment("v3")
                .addPathSegment(Long.toString(profile))
                .build();

        log.debug("Built URI: {}", url);

        Request request = new Request.Builder()
                .delete()
                .header(RuneLiteAPI.RUNELITE_AUTH, uuid.toString())
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                log.warn("error deleting profile {}", profile, e);
            }

            @Override
            public void onResponse(Call call, Response response)
            {
                log.debug("deleted profile {}", profile);
                response.close();
            }
        });
    }

    public void rename(long profile, String name)
    {
        HttpUrl url = apiBase.newBuilder()
                .addPathSegment("config")
                .addPathSegment("v3")
                .addPathSegment(Long.toString(profile))
                .addPathSegment("name")
                .build();

        log.debug("Built URI: {}", url);

        Request request = new Request.Builder()
                .post(RequestBody.create(null, name))
                .header(RuneLiteAPI.RUNELITE_AUTH, uuid.toString())
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                log.warn("error renaming profile {}", profile, e);
            }

            @Override
            public void onResponse(Call call, Response response)
            {
                if (!response.isSuccessful())
                {
                    log.debug("unable to rename profile {} to {}", profile, name);
                }
                else
                {
                    log.debug("renamed profile {} to {}", profile, name);
                }
                response.close();
            }
        });
    }
}