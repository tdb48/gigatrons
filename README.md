# About
TLDR this was a repostitory for a Nex and ToA bot on OSRS, written back in 2023-2024.

I wanted to open source our project as I lost interest in botting a few years ago and I think people with similar interests could greatly benefit from it. I wrote these bots together with my friend Liam (Mcneill), who passed away in March 2025, and I kinda wanted this up as remembrance for him + as my last big project before AI.

## ToA
We believe this to be the absolute first ToA bot, with the first drop being on 03/03/2023 only a few months after ToA release. We ran the bot doing 370 in budget gear (~400m). Since we were the first bots, we were able to run it 24/7 for months and get accounts as much as up to 3000 KC. I believe basically every room will be broken now since ToA has had so many updates, but it could still be good inspiration!

## Nex
This bot is built to do instanced duo Nex and uses a socket for communication between accounts so you could run it on separate VMs/PCs. It does an extremely good job at taking as little damage as possible, so well that it can sometimes do two kill trips in budget gear. We wrote a relatively complex system for venoming and tracking venom ticks on reavers to farm killcount as efficiently as possible. 
Breaks have to be agreed on between accounts. Needing a break is a flag that goes over the socket, so an account wanting to stop is something the other finds out about, instead of one client logging out and leaving the other alone in an instance.

## The task system

Both bots are just a big list of small tasks that get checked every game tick, ToA has 96 of them and Nex has 38. Every tick it works out which ones are active and runs them in priority order. There is also a bit of randomness on when it actually acts, it picks a random client tick to wait inside the game tick and the more tasks that are active the smaller that window gets, so when it is busy it gives up the randomness to make sure everything still happens in time.

## Pathfinding

The plugin ships a collision map of the entire game world in a ~3mb (gzipped) bitmap. Every tile packs down into a single number with the walkable directions stored as bits on top of it. That means asking if you can walk east from a tile is just a lookup rather than anything smart, and it means the bot can path anywhere in the game instead of only inside the rooms we had mapped out ourselves. 
