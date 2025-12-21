package com.veilorigins.registry;

import com.veilorigins.api.*;
import com.veilorigins.origins.veilborn.*;
import com.veilorigins.origins.stoneheart.*;
import com.veilorigins.origins.feralkin.*;
import com.veilorigins.origins.riftwalker.*;
import com.veilorigins.origins.umbrakin.*;
import com.veilorigins.origins.voidtouched.*;
import com.veilorigins.origins.frostborn.*;
import com.veilorigins.origins.cindersoul.*;
import com.veilorigins.origins.tidecaller.*;
import com.veilorigins.origins.starborne.*;
import com.veilorigins.origins.skyborn.*;
import com.veilorigins.origins.mycomorph.*;
import com.veilorigins.origins.crystalline.*;
import com.veilorigins.origins.technomancer.*;
import com.veilorigins.origins.ethereal.*;
import com.veilorigins.origins.vampire.*;
import com.veilorigins.origins.vampling.*;
import com.veilorigins.origins.werewolf.*;
import com.veilorigins.origins.wolfling.*;
import com.veilorigins.origins.dryad.*;
import com.veilorigins.origins.necromancer.*;

public class ModOrigins {

        public static void register() {
                registerVeilborn();
                registerStoneheart();
                registerFeralkin();
                registerRiftwalker();
                registerUmbrakin();
                registerVoidtouched();
                registerFrostborn();
                registerCindersoul();
                registerTidecaller();
                registerStarborne();
                registerSkyborn();
                registerMycomorph();
                registerCrystalline();
                registerTechnomancer();
                registerEthereal();
                registerVampire();
                registerVampling();
                registerWerewolf();
                registerWolfling();
                registerDryad();
                registerNecromancer();
        }

        private static void registerVeilborn() {
                Origin veilborn = new OriginBuilder("veil_origins:veilborn")
                                .setDisplayName("Veilborn")
                                .setDescription(
                                                "Masters of the boundary between life and death. You exist between worlds, commanding soul essence and phasing through reality.")
                                .setImpactLevel(ImpactLevel.HIGH)
                                .setHealthModifier(1.0f)
                                .setSpeedModifier(1.0f)
                                .addAbility(new VeilStepAbility())
                                .addAbility(new SoulHarvestAbility())
                                .addPassive(new SpectralFormPassive())
                                .addPassive(new LifeDrainPassive())
                                .addPassive(new VeilbornWeaknessesPassive())
                                .setResourceType(new ResourceType("soul_energy", 100, 0.5f))
                                .build();

                VeilOriginsAPI.registerOrigin(veilborn);
        }

        private static void registerStoneheart() {
                Origin stoneheart = new OriginBuilder("veil_origins:stoneheart")
                                .setDisplayName("Stoneheart")
                                .setDescription(
                                                "Living stone, immovable and indestructible. You're incredibly durable but slow and heavy.")
                                .setImpactLevel(ImpactLevel.LOW)
                                .setHealthModifier(1.5f) // 30 HP
                                .setSpeedModifier(0.5f) // 50% slower
                                .addAbility(new SeismicSlamAbility())
                                .addAbility(new StoneSkinAbility())
                                .addPassive(new LivingMountainPassive())
                                .addPassive(new EarthAffinityPassive())
                                .addPassive(new StoneheartWeaknessesPassive())
                                .setResourceType(new ResourceType("stone_armor", 100, 1.0f))
                                .build();

                VeilOriginsAPI.registerOrigin(stoneheart);
        }

        private static void registerFeralkin() {
                Origin feralkin = new OriginBuilder("veil_origins:feralkin")
                                .setDisplayName("Feralkin")
                                .setDescription(
                                                "Beast shapeshifters with animal instincts. Superior senses and mobility, but civilized tools feel unnatural.")
                                .setImpactLevel(ImpactLevel.LOW)
                                .setHealthModifier(1.0f)
                                .setSpeedModifier(1.1f) // Slightly faster
                                .addAbility(new PrimalRoarAbility())
                                .addAbility(new BeastFormAbility())
                                .addPassive(new PredatorSensesPassive())
                                .addPassive(new NaturalWeaponsPassive())
                                .addPassive(new FeralkinWeaknessesPassive())
                                .setResourceType(new ResourceType("feral_instinct", 100, 0.3f))
                                .build();

                VeilOriginsAPI.registerOrigin(feralkin);
        }

        private static void registerRiftwalker() {
                Origin riftwalker = new OriginBuilder("veil_origins:riftwalker")
                                .setDisplayName("Riftwalker")
                                .setDescription(
                                                "Dimensional travelers who bend space itself. You can teleport vast distances and access a pocket dimension.")
                                .setImpactLevel(ImpactLevel.HIGH)
                                .setHealthModifier(1.0f)
                                .setSpeedModifier(1.0f)
                                .addAbility(new DimensionalHopAbility())
                                .addAbility(new PocketDimensionAbility())
                                .addPassive(new SpatialAwarenessPassive())
                                .addPassive(new UnstableFormPassive())
                                .addPassive(new RiftwalkerWeaknessesPassive())
                                .setResourceType(new ResourceType("dimensional_stability", 100, 0.8f))
                                .build();

                VeilOriginsAPI.registerOrigin(riftwalker);
        }

        private static void registerUmbrakin() {
                Origin umbrakin = new OriginBuilder("veil_origins:umbrakin")
                                .setDisplayName("Umbrakin")
                                .setDescription(
                                                "Shadow manipulators, creatures of darkness. Night is your domain where you're strongest, but sunlight burns you.")
                                .setImpactLevel(ImpactLevel.HIGH)
                                .setHealthModifier(1.0f)
                                .setSpeedModifier(1.0f)
                                .addAbility(new ShadowMeldAbility())
                                .addAbility(new DarknessBoltAbility())
                                .addPassive(new ShadowFormPassive())
                                .addPassive(new PhotophobiaPassive())
                                .setResourceType(new ResourceType("shadow_energy", 100, 0.5f))
                                .build();

                VeilOriginsAPI.registerOrigin(umbrakin);
        }

        private static void registerVoidtouched() {
                Origin voidtouched = new OriginBuilder("veil_origins:voidtouched")
                                .setDisplayName("Voidtouched")
                                .setDescription(
                                                "Corrupted by the void, reality benders. You can manipulate reality itself, but you're unstable and dangerous.")
                                .setImpactLevel(ImpactLevel.HIGH)
                                .setHealthModifier(1.0f)
                                .setSpeedModifier(1.0f)
                                .addAbility(new VoidTearAbility())
                                .addAbility(new RealityShiftAbility())
                                .addPassive(new VoidWalkerPassive())
                                .addPassive(new RealityDistortionPassive())
                                .addPassive(new VoidtouchedWeaknessesPassive())
                                .setResourceType(new ResourceType("void_corruption", 100, 0.3f))
                                .build();

                VeilOriginsAPI.registerOrigin(voidtouched);
        }

        private static void registerFrostborn() {
                Origin frostborn = new OriginBuilder("veil_origins:frostborn")
                                .setDisplayName("Frostborn")
                                .setDescription(
                                                "Children of winter, masters of ice and cold. You command ice and thrive in cold, but heat and fire are lethal.")
                                .setImpactLevel(ImpactLevel.MEDIUM)
                                .setHealthModifier(1.0f)
                                .setSpeedModifier(1.0f)
                                .addAbility(new IceSpikeAbility())
                                .addAbility(new BlizzardAbility())
                                .addPassive(new PermafrostPassive())
                                .addPassive(new ColdAuraPassive())
                                .addPassive(new FrostbornWeaknessesPassive())
                                .setResourceType(new ResourceType("chill_factor", 100, 0.6f))
                                .build();

                VeilOriginsAPI.registerOrigin(frostborn);
        }

        private static void registerCindersoul() {
                Origin cindersoul = new OriginBuilder("veil_origins:cindersoul")
                                .setDisplayName("Cindersoul")
                                .setDescription(
                                                "Born from volcanic fury, masters of heat and flame. You thrive in heat and can manipulate flames, but water and cold are your mortal enemies.")
                                .setImpactLevel(ImpactLevel.MEDIUM)
                                .setHealthModifier(1.0f)
                                .setSpeedModifier(1.0f)
                                .addAbility(new FlameBurstAbility())
                                .addAbility(new LavaWalkAbility())
                                .addPassive(new HeatAffinityPassive())
                                .addPassive(new ThermalVisionPassive())
                                .addPassive(new CindersoulWeaknessesPassive())
                                .setResourceType(new ResourceType("internal_heat", 100, 0.5f)) // Recharges near fire
                                                                                               // check in handler
                                .build();

                VeilOriginsAPI.registerOrigin(cindersoul);
        }

        private static void registerTidecaller() {
                Origin tidecaller = new OriginBuilder("veil_origins:tidecaller")
                                .setDisplayName("Tidecaller")
                                .setDescription(
                                                "The ocean is your home. You command water and breathe beneath the waves, but land weakens you.")
                                .setImpactLevel(ImpactLevel.MEDIUM)
                                .setHealthModifier(1.0f)
                                .setSpeedModifier(1.0f)
                                .addAbility(new TidalWaveAbility())
                                .addAbility(new AquaBubbleAbility())
                                .addPassive(new OceansGiftPassive())
                                .addPassive(new HydrationDependencyPassive())
                                .addPassive(new TidecallerWeaknessesPassive())
                                .setResourceType(new ResourceType("hydration", 100, 0.0f)) // Manually managed in
                                                                                           // EventHandler
                                .build();

                VeilOriginsAPI.registerOrigin(tidecaller);
        }

        private static void registerStarborne() {
                Origin starborne = new OriginBuilder("veil_origins:starborne")
                                .setDisplayName("Starborne")
                                .setDescription("Fallen from the cosmos. Flight comes naturally, and darkness is your enemy.")
                                .setImpactLevel(ImpactLevel.MEDIUM)
                                .setHealthModifier(0.75f) // 15 HP
                                .setSpeedModifier(1.0f)
                                .addAbility(new CelestialDashAbility())
                                .addAbility(new StarlightBeaconAbility())
                                .addPassive(new WingsOfLightPassive())
                                .addPassive(new SolarPoweredPassive())
                                .addPassive(new StarborneWeaknessesPassive())
                                .setResourceType(new ResourceType("stellar_energy", 100, 0.5f))
                                .build();

                VeilOriginsAPI.registerOrigin(starborne);
        }

        private static void registerSkyborn() {
                Origin skyborn = new OriginBuilder("veil_origins:skyborn")
                                .setDisplayName("Skyborn")
                                .setDescription(
                                                "Wind masters who never touch the ground. Heights empower you, but the ground weakens your abilities.")
                                .setImpactLevel(ImpactLevel.MEDIUM)
                                .setHealthModifier(1.0f)
                                .setSpeedModifier(1.0f)
                                .addAbility(new WindBlastAbility())
                                .addAbility(new UpdraftAbility())
                                .addPassive(new CloudStriderPassive())
                                .addPassive(new AltitudeAffinityPassive())
                                .addPassive(new SkybornWeaknessesPassive())
                                .setResourceType(new ResourceType("altitude_bonus", 100, 0.0f))
                                .build();

                VeilOriginsAPI.registerOrigin(skyborn);
        }

        private static void registerMycomorph() {
                Origin mycomorph = new OriginBuilder("veil_origins:mycomorph")
                                .setDisplayName("Mycomorph")
                                .setDescription(
                                                "Part mushroom, part humanoid. You spread fungal growth wherever you go and can communicate with mushroom colonies.")
                                .setImpactLevel(ImpactLevel.MEDIUM)
                                .setHealthModifier(1.0f)
                                .setSpeedModifier(1.0f)
                                .addAbility(new SporeCloudAbility())
                                .addAbility(new FungalNetworkAbility())
                                .addPassive(new DecomposerPassive())
                                .addPassive(new PhotosynthesisPassive())
                                .addPassive(new MycomorphWeaknessesPassive())
                                .setResourceType(new ResourceType("spore_count", 100, 0.2f))
                                .build();

                VeilOriginsAPI.registerOrigin(mycomorph);
        }

        private static void registerCrystalline() {
                Origin crystalline = new OriginBuilder("veil_origins:crystalline")
                                .setDisplayName("Crystalline")
                                .setDescription("Living crystal with resonant powers. Brittle but beautiful.")
                                .setImpactLevel(ImpactLevel.LOW)
                                .setHealthModifier(1.0f)
                                .setSpeedModifier(0.75f) // 25% slower
                                .addAbility(new CrystalSpikeAbility())
                                .addAbility(new OreResonanceAbility())
                                .addPassive(new CrystalBodyPassive())
                                .addPassive(new EnergyStoragePassive())
                                .setResourceType(new ResourceType("crystal_charge", 100, 0.0f))
                                .build();

                VeilOriginsAPI.registerOrigin(crystalline);
        }

        private static void registerTechnomancer() {
                Origin technomancer = new OriginBuilder("veil_origins:technomancer")
                                .setDisplayName("Technomancer")
                                .setDescription("Cybernetic being, part flesh part machine. Master of redstone.")
                                .setImpactLevel(ImpactLevel.MEDIUM)
                                .setHealthModifier(1.0f)
                                .setSpeedModifier(1.0f)
                                .addAbility(new RedstonePulseAbility())
                                .addAbility(new OverclockAbility())
                                .addPassive(new MachineAffinityPassive())
                                .addPassive(new CyborgResiliencePassive())
                                .setResourceType(new ResourceType("power_cells", 100, 0.1f))
                                .build();

                VeilOriginsAPI.registerOrigin(technomancer);
        }

        private static void registerEthereal() {
                Origin ethereal = new OriginBuilder("veil_origins:ethereal")
                                .setDisplayName("Ethereal")
                                .setDescription("Ghostly being phased between dimensions. Can pass through objects.")
                                .setImpactLevel(ImpactLevel.HIGH)
                                .setHealthModifier(1.0f)
                                .setSpeedModifier(1.0f)
                                .addAbility(new PhaseShiftAbility())
                                .addAbility(new PossessionAbility())
                                .addPassive(new IncorporealPassive())
                                .addPassive(new GhostlyPassive())
                                .setResourceType(new ResourceType("phase_energy", 100, 0.5f))
                                .build();

                VeilOriginsAPI.registerOrigin(ethereal);
        }

        private static void registerVampire() {
                Origin vampire = new OriginBuilder("veil_origins:vampire")
                                .setDisplayName("Vampire")
                                .setDescription(
                                                "Immortal blood drinker. You are powerful at night and can drain life, but sunlight is lethal.")
                                .setImpactLevel(ImpactLevel.HIGH)
                                .setHealthModifier(1.2f)
                                .setSpeedModifier(1.1f)
                                .addAbility(new BloodDrainAbility())
                                .addAbility(new BatFormAbility())
                                .addPassive(new VampirePassive())
                                .addPassive(new VampiricDoubleJumpPassive(6.0f))
                                .addPassive(new BloodDrainGazePassive(true))
                                .setResourceType(new ResourceType("blood_essence", 100, 0.3f))
                                .build();

                VeilOriginsAPI.registerOrigin(vampire);
        }

        private static void registerVampling() {
                Origin vampling = new OriginBuilder("veil_origins:vampling")
                                .setDisplayName("Vampling")
                                .setDescription(
                                                "Lesser vampire with diluted bloodline. You have some vampiric powers but are weakened in sunlight rather than burned.")
                                .setImpactLevel(ImpactLevel.MEDIUM)
                                .setHealthModifier(1.0f)
                                .setSpeedModifier(1.05f)
                                .addAbility(new LifeStealAbility())
                                .addPassive(new VamplingPassive())
                                .addPassive(new VampiricDoubleJumpPassive(3.0f))
                                .addPassive(new BloodDrainGazePassive(false))
                                .setResourceType(new ResourceType("blood_essence", 100, 0.5f))
                                .build();

                VeilOriginsAPI.registerOrigin(vampling);
        }

        private static void registerWerewolf() {
                Origin werewolf = new OriginBuilder("veil_origins:werewolf")
                                .setDisplayName("Werewolf")
                                .setDescription(
                                                "Cursed shapeshifter. You transform into a powerful beast at night, especially during the full moon.")
                                .setImpactLevel(ImpactLevel.HIGH)
                                .setHealthModifier(1.3f)
                                .setSpeedModifier(1.15f)
                                .addAbility(new WolfFormAbility())
                                .addAbility(new HowlAbility())
                                .addPassive(new WerewolfPassive())
                                .setResourceType(new ResourceType("lunar_power", 100, 0.4f))
                                .build();

                VeilOriginsAPI.registerOrigin(werewolf);
        }

        private static void registerWolfling() {
                Origin wolfling = new OriginBuilder("veil_origins:wolfling")
                                .setDisplayName("Wolfling")
                                .setDescription(
                                                "Lesser werewolf with partial curse. You have enhanced senses and speed at night but lack the full transformation.")
                                .setImpactLevel(ImpactLevel.LOW)
                                .setHealthModifier(1.1f)
                                .setSpeedModifier(1.1f)
                                .addAbility(new PackHowlAbility())
                                .addPassive(new WolflingPassive())
                                .setResourceType(new ResourceType("lunar_power", 100, 0.6f))
                                .build();

                VeilOriginsAPI.registerOrigin(wolfling);
        }

        private static void registerDryad() {
                Origin dryad = new OriginBuilder("veil_origins:dryad")
                                .setDisplayName("Dryad")
                                .setDescription(
                                                "Nature spirit and forest guardian. You command plants and commune with nature, but fire and dry climates are hostile to you.")
                                .setImpactLevel(ImpactLevel.MEDIUM)
                                .setHealthModifier(1.0f)
                                .setSpeedModifier(1.0f)
                                .addAbility(new EntanglingRootsAbility())
                                .addAbility(new NaturesBlessingAbility())
                                .addPassive(new SunlightPhotosynthesisPassive())
                                .addPassive(new ForestBondPassive())
                                .addPassive(new DryadWeaknessesPassive())
                                .setResourceType(new ResourceType("nature_energy", 100, 0.5f))
                                .build();

                VeilOriginsAPI.registerOrigin(dryad);
        }

        private static void registerNecromancer() {
                Origin necromancer = new OriginBuilder("veil_origins:necromancer")
                                .setDisplayName("Necromancer")
                                .setDescription(
                                                "Master of death and undeath. You command the dead and drain life from the living, but sunlight weakens you and holy places are harmful.")
                                .setImpactLevel(ImpactLevel.HIGH)
                                .setHealthModifier(0.9f) // Slightly less health
                                .setSpeedModifier(1.0f)
                                .addAbility(new RaiseDeadAbility())
                                .addAbility(new LifeSiphonAbility())
                                .addPassive(new UndeadMasteryPassive())
                                .addPassive(new DeathAuraPassive())
                                .addPassive(new NecromancerWeaknessesPassive())
                                .setResourceType(new ResourceType("death_essence", 100, 0.4f))
                                .build();

                VeilOriginsAPI.registerOrigin(necromancer);
        }
}
