[![Discord link to the "LopyMine's Project" discord server](https://cdn.modrinth.com/data/cached_images/21f178aff2b64844fefeaf94a3a3a418440fd43f.png)](https://discord.gg/NZzxdkrV4s) [![Github Link-Banner](https://cdn.modrinth.com/data/cached_images/d87060a9e4522786e45b4b54d10853a1b4a91b10.png)](https://github.com/LopyMine/Item-Particles) [![Support Link-Banner [Boosty]](https://cdn.modrinth.com/data/cached_images/dce91fef079649dee277c52a998fc068e745e99e.png)](https://boosty.to/lopymine/donate)

[![CurseForge Link-Banner](https://cdn.modrinth.com/data/cached_images/c39affcb732c8b1a4fe189e79898c686da3d63e2.png)](https://www.curseforge.com/minecraft/mc-mods/item-particles) [![Modrinth Link-Banner](https://cdn.modrinth.com/data/cached_images/9991553f6a20e5105b9b153b8d817bc3630c18a8.png)](https://modrinth.com/mod/item-particles)

!["Description" Title](https://github.com/LopyMine/Profile/blob/main/files/Template%20Resource%20Pack/image2.webp?raw=true)

**Item Particles** — Visual mod which adds a lot of new particles for your world items! With this mod they will look more interactive and dynamic! Completely client-side.

This mod is based on another my mod: [**Inventory Particles**](https://modrinth.com/mod/inventory-particles),

So, if you’re already familiar with it, expect to see the same particles, just in the world!

### 1) Third Person

![Third Person](https://cdn.modrinth.com/data/cached_images/28fa27859a6d1c5bd03dd8f0f0736065be17a8fb.png)

### 2) First Person

![First Person](https://cdn.modrinth.com/data/cached_images/95511bdfccbe30ae7e6485801e3382ac8064a136_0.webp)

### 3) Dropped Items

![Dropped Items](https://cdn.modrinth.com/data/cached_images/2b7f3f2b21157dd6cdcf87937e6993864c13ea0c.png)

### 4-5) Shelf & Frame Items

![Shelfs & Frames](https://cdn.modrinth.com/data/cached_images/7cb244a72a055aab5e7c51030b332a793c986eb8.png)

# 🤔 "Seems too many particles"
If it seems like there are too many of them, you can easily adjust their count and cooldown in the configuration menu, to perfectly complement your gameplay!

![Replace this with a description](https://cdn.modrinth.com/data/cached_images/61941cb481b5454793bb6ce13d015c88c2d7ecdc.png)

![Just decoration here](https://cdn.modrinth.com/data/cached_images/e66456118e8d1bd51d25bd166f33d70e008a0fe6_0.webp)

!["Compatible" Title](https://github.com/LopyMine/Profile/blob/main/files/Template%20Resource%20Pack/compatiblev2.webp?raw=true)

You’re probably wondering how the mod handles items from other mods? Well, the mod features its **own system for generating "family" particles!**

**You don't need to do anything — the mod handles it all for you!**

**In short:** The mod includes config files that define item "categories" and "varieties." It analyzes the items and, based on these configs, assigns particles, potentially recolors them, and sets up unique spawn areas for the items themselves.

<details>
<summary> Example Family Config </summary>

```json
{
	"keywords": [
		"powder",
		"gunpowder",
		"sugar",
		"dust",
		"dye",
		"ash",
		"ashen"
	],
	"tags": [
		"c:dusts"
	],
	"particles": [
		{
			"id": "family/dust/dust",
			"textures": [
				"item_particles:dust/dust_0.png",
				"item_particles:dust/dust_1.png",
				"item_particles:dust/dust_2.png"
			],
			"texture_generation_mode": {
				"luminance": true,
				"saturation": true,
				"frequency": true
			},
			"spawn_count": [0, 2],
			"spawn_frequency": [10, 20],
			"speed_coefficient": 0.25,
			"nbt_conditions_match": "any",
			"nbt_conditions": []
		}
	],
	"family_groups": [],
	"compatible": false,
	"priority": 1500
}

```
</details>

![Just decoration here](https://cdn.modrinth.com/data/cached_images/e66456118e8d1bd51d25bd166f33d70e008a0fe6_0.webp)

!["Configurable" Title](https://github.com/LopyMine/Profile/blob/main/files/Template%20Resource%20Pack/image.webp?raw=true)

**All particles** are **data driven**! This means that you can add more particles just by using resource packs and some basic configs!

**You can customize:**
1) Physics (acceleration, braking, impulses, etc.)
2) Rotations (actual and visual)
3) Sizes (+ animations)
4) Colors (+ animations)
5) Texture (+ animations)
6) Holders (items from which they will spawn)
7) Spawn Area (just with additional texture)
8) Life Time (ticks)
9) And other little things ^^

[**See the official wiki to learn how thing works here!**](https://github.com/LopyMine/Inventory-Particles/wiki)

<details>
<summary> Example Config </summary>

```json
{
	"life_time": 100,
	"animation_type": "stretch",
	"animation_speed": 1.0,

	"size": {
		"width": 8.0,
		"height": 8.0
	},
	// or
	"size": {
		// https://nicmulvaney.com/easing
		"interpolation": "ease_in_quint",
		"sizes": {
			"0": {
				"width": 16.0,
				"height": 16.0,
				// Overrides "ease_in_quint"
				"interpolation": "linear"
			},
			"50": {
				"width": 32.0,
				"height": 32.0
			},
			"100": {
				"width": 64.0,
				"height": 64.0
			}
		}
	},

	"textures": [
		"item_particles:example/texture_0.png",
		"item_particles:example/texture_1.png"
	],
	
	"holders": [
		{
			"item": "minecraft:potion",
            
			"spawn_area": "example_spawn_area.png",
			// or
			"spawn_area": "#full",
			// or
			"spawn_area": {
				"minecraft:block/grass_block_side": "particles_area_on_side.png",
				"minecraft:block/grass_block_top": "particles_area_on_top.png"
			},
			// or
			"spawn_area": {
				"minecraft:block/grass_block_side": "#full",
				"minecraft:block/grass_block_top": "particles_area_on_top.png"
			},
            
			"spawn_count": [0, 1],
			"spawn_frequency": [5, 20],
			"speed_coefficient": 0.3,

			"color": "#AARRGGBB",
			// or
			"color": {
				// mixed, random, random_static, gradient, gradient_random_static, gradient_loop, gradient_bounce
				"mode": "gradient",
				"values": ["#FF8CC091", "#003700", "nbt", "nbt_list"],
				"speed": 5.0
			},

			"nbt_conditions_match": "none",
			"nbt_conditions": [
				{
					"this_name": "components",
					"this_type": "object",
					"next_match": "any",
					"next": {
						"this_name": "minecraft:potion_contents",
						"this_type": "object",
						"next_match": "any",
						"next": [
							{
								"this_name": "potion",
								"this_type": "string",
								"check_value": "minecraft:water"
							}
						]
					}
				}
			]
		}
	],
	
	"physics": {
		"base": {
			"x_speed": {
				"impulse": [0.0, 0.0],
				"impulse_bidirectional": false,
				"acceleration": 0.0,
				"acceleration_bidirectional": false,
				"max_acceleration": [-100.0, 100.0],
				"braking": 0.0,
				"turbulence": [0.0, 0.0],
				"impulse_inherit_coefficient": 1.0
			},
			"y_speed": {
				"impulse": [0.0, 0.0],
				"impulse_bidirectional": false,
				"acceleration": 0.0,
				"acceleration_bidirectional": false,
				"max_acceleration": [-100.0, 100.0],
				"braking": 0.0,
				"turbulence": [0.0, 0.0],
				"impulse_inherit_coefficient": 1.0
			},
			"angle_speed": {
				"impulse": [1.0, 1.5],
				"impulse_bidirectional": false,
				"acceleration": 0.0,
				"acceleration_bidirectional": false,
				"max_acceleration": [-100.0, 100.0],
				"braking": 0.0,
				"turbulence": [0.0, 0.0]
			}
		},
		"rotation": {
			"particle": {
				"spawn_angle": [0.0, 360.0],
                "spawn_azimuth": [0.0, 360.0],
				"rotate_in_movement_direction": false,
				"speed": {
					"impulse": [0.0, 0.0],
					"impulse_bidirectional": false,
					"acceleration": 0.0,
					"acceleration_bidirectional": false,
					"max_acceleration": 0.0,
					"braking": 0.0,
					"turbulence": [0.0, 0.0]
				}
			},
			"texture": {
				"spawn_angle": [0.0, 0.0],
                "spawn_azimuth": [0.0, 360.0],
				"rotate_in_movement_direction": false,
				"speed": {
					"impulse": [0.0, 0.0],
					"impulse_bidirectional": true,
					"acceleration": 0.0,
					"acceleration_bidirectional": false,
					"max_acceleration": 0.0,
					"braking": 0.0,
					"turbulence": [0.0, 0.0]
				}
			}
		}
	}
}
```
</details>

![Just decoration here](https://cdn.modrinth.com/data/cached_images/e66456118e8d1bd51d25bd166f33d70e008a0fe6_0.webp)

![Showcase](https://cdn.modrinth.com/data/cached_images/c681a00eaa95d93c03b28566a266f2aad3707e5f.png)

### ![Icon](https://cdn.modrinth.com/data/cached_images/9111988448f778aaff9ac4477e463343fe4dc7e4.png) Want to support mod and authors? Just tell everyone about this mod!

Yeah, you got it right. Just by advertising, you will support the mod and the creators well. The more people will know about this mod, the more downloads it will have, more downloads will give good motivation to authors and increase income from the site (literally free donation). **Remember, advertising must not be intrusive and annoiyng!**

### What you can do?
- Make a video review / advertisement
- Tell your friends about this mod
- Just download this mod (if you want to play with it :D)

# 📑 Licensing
[**See the original mod repository.**](https://github.com/LopyMine/Item-Particles)