package com.enzo.timeclock;

import com.enzo.timeclock.item.TimeClockItem;
import com.enzo.timeclock.time.TimeManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class TimeClockMod implements ModInitializer {
    public static final String MOD_ID = "timeclock";
    public static final Identifier CONTROL = new Identifier(MOD_ID, "control");
    public static final Item CLOCK = Registry.register(
            Registries.ITEM,
            new Identifier(MOD_ID, "time_clock"),
            new TimeClockItem(new Item.Settings().maxCount(1))
    );

    @Override
    public void onInitialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> entries.add(CLOCK));

        ServerTickEvents.START_SERVER_TICK.register(TimeManager::onStartTick);
        ServerTickEvents.END_SERVER_TICK.register(TimeManager::onEndTick);

        ServerPlayNetworking.registerGlobalReceiver(CONTROL, (server, player, handler, buf, responseSender) -> {
            if (buf.readableBytes() < 2) {
                return;
            }
            int action = buf.readUnsignedByte();
            boolean pressed = buf.readBoolean();
            server.execute(() -> TimeManager.control(player, action, pressed));
        });
    }
}
