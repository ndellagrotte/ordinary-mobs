package fathertoast.specialmobs.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.network.NetHandlerPlayServer;

/** Exposes the server's flying check, which has to be reset when a mob picks a player up in mid-air. */
@Mixin(NetHandlerPlayServer.class)
public interface NetHandlerPlayServerAccessor {

    @Accessor("floating")
    void specialmobs$setFloating(boolean floating);

    @Accessor("floatingTickCount")
    void specialmobs$setFloatingTickCount(int ticks);

}
