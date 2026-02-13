package com.modernexpshare;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.battles.BattleFledEvent;
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent;
import com.cobblemon.mod.common.api.events.pokemon.EvGainedEvent;
import com.cobblemon.mod.common.api.events.pokemon.ExperienceGainedEvent;
import com.cobblemon.mod.common.api.pokemon.experience.BattleExperienceSource;
import com.cobblemon.mod.common.api.pokemon.experience.SidemodExperienceSource;
import com.cobblemon.mod.common.api.pokemon.stats.BattleEvSource;
import com.cobblemon.mod.common.api.pokemon.stats.EvSource;
import com.cobblemon.mod.common.api.pokemon.stats.SidemodEvSource;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

final class ModernExpShareLogic {
    private static final Identifier EXP_SHARE_ID = Identifier.of("cobblemon", "exp_share");
    private static final SidemodExperienceSource SIDEMOD_EXP_SOURCE = new SidemodExperienceSource(ModernExpShareMod.MOD_ID);

    private static final ConcurrentHashMap<UUID, Set<UUID>> battleToExpReceivers = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<UUID, Set<UUID>> battleToEvReceivers = new ConcurrentHashMap<>();

    private ModernExpShareLogic() {
    }

    static void registerCobblemonHandlers() {
        CobblemonEvents.EXPERIENCE_GAINED_EVENT_PRE.subscribe(Priority.NORMAL, (ExperienceGainedEvent.Pre event) -> {
            try {
                handleExperiencePre(event);
            } catch (Throwable t) {
                ModernExpShareMod.LOGGER.error("Error in EXPERIENCE_GAINED_EVENT_PRE handler", t);
            }
        });

        CobblemonEvents.EV_GAINED_EVENT_PRE.subscribe(Priority.NORMAL, (EvGainedEvent.Pre event) -> {
            try {
                handleEvPre(event);
            } catch (Throwable t) {
                ModernExpShareMod.LOGGER.error("Error in EV_GAINED_EVENT_PRE handler", t);
            }
        });

        CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.NORMAL, (BattleVictoryEvent event) -> cleanupBattle(event.getBattle()));
        CobblemonEvents.BATTLE_FLED.subscribe(Priority.NORMAL, (BattleFledEvent event) -> cleanupBattle(event.getBattle()));
    }

    private static void cleanupBattle(PokemonBattle battle) {
        if (battle == null) {
            return;
        }
        UUID battleId = battle.getBattleId();
        battleToExpReceivers.remove(battleId);
        battleToEvReceivers.remove(battleId);
    }

    private static void handleExperiencePre(ExperienceGainedEvent.Pre event) {
        if (event == null) {
            return;
        }

        var source = event.getSource();
        if (source == null || source.isSidemod()) {
            return;
        }
        if (!(source instanceof BattleExperienceSource battleSource)) {
            return;
        }

        Pokemon battlePokemon = event.getPokemon();
        if (battlePokemon == null) {
            return;
        }

        ServerPlayerEntity owner = (ServerPlayerEntity) battlePokemon.getOwnerPlayer();
        if (owner == null) {
            return;
        }
        if (!playerHasExpShare(owner)) {
            return;
        }

        UUID battleId = battleSource.getBattle().getBattleId();
        battleToExpReceivers.computeIfAbsent(battleId, ignored -> ConcurrentHashMap.newKeySet())
                .add(battlePokemon.getUuid());

        int baseExp = event.getExperience();
        int sharedExp = (int) Math.floor(baseExp * ModernExpShareConfig.get().sharedExpMultiplier);
        if (sharedExp <= 0) {
            return;
        }

        Set<UUID> expReceivers = battleToExpReceivers.get(battleId);
        PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(owner);
        for (Pokemon partyPokemon : party) {
            if (partyPokemon == null) {
                continue;
            }
            if (partyPokemon.isFainted()) {
                continue;
            }
            if (expReceivers != null && expReceivers.contains(partyPokemon.getUuid())) {
                continue;
            }
            partyPokemon.addExperienceWithPlayer(owner, SIDEMOD_EXP_SOURCE, sharedExp);
        }
    }

    private static void handleEvPre(EvGainedEvent.Pre event) {
        if (event == null) {
            return;
        }

        EvSource source = event.getSource();
        if (source == null || source.isSidemod()) {
            return;
        }
        if (!(source instanceof BattleEvSource battleSource)) {
            return;
        }

        Pokemon battlePokemon = event.getPokemon();
        if (battlePokemon == null) {
            return;
        }

        ServerPlayerEntity owner = (ServerPlayerEntity) battlePokemon.getOwnerPlayer();
        if (owner == null) {
            return;
        }
        if (!playerHasExpShare(owner)) {
            return;
        }

        UUID battleId = battleSource.getBattle().getBattleId();
        battleToEvReceivers.computeIfAbsent(battleId, ignored -> ConcurrentHashMap.newKeySet())
                .add(battlePokemon.getUuid());

        int baseEv = event.getAmount();
        int sharedEv = (int) Math.floor(baseEv * ModernExpShareConfig.get().sharedEvMultiplier);
        if (sharedEv <= 0) {
            return;
        }

        Set<UUID> evReceivers = battleToEvReceivers.get(battleId);
        PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(owner);
        for (Pokemon partyPokemon : party) {
            if (partyPokemon == null) {
                continue;
            }
            if (partyPokemon.isFainted()) {
                continue;
            }
            if (evReceivers != null && evReceivers.contains(partyPokemon.getUuid())) {
                continue;
            }

            partyPokemon.getEvs().add(event.getStat(), sharedEv, new SidemodEvSource(ModernExpShareMod.MOD_ID, partyPokemon));
        }
    }

    private static boolean playerHasExpShare(ServerPlayerEntity player) {
        Item expShareItem = Registries.ITEM.get(EXP_SHARE_ID);
        PlayerInventory inv = player.getInventory();
        for (int i = 0; i < inv.size(); i++) {
            if (inv.getStack(i).isOf(expShareItem)) {
                return true;
            }
        }
        return false;
    }
}
