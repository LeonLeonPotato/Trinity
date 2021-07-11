package me.leon.trinity.events.main;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.TrinityEvent;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelEnderCrystal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;

public class EventRenderEntityCrystal extends TrinityEvent {
    public final ModelEnderCrystal modelBase;
    public EntityEnderCrystal entityIn;
    public float limbSwing;
    public float limbSwingAmount;
    public float ageInTicks;
    public float netHeadYaw;
    public float headPitch;
    public float scale;

    public EventRenderEntityCrystal(EventStage stage, EntityEnderCrystal crystal, ModelEnderCrystal modelBase, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        super(stage);
        this.modelBase = modelBase;
        this.entityIn = crystal;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.ageInTicks = ageInTicks;
        this.netHeadYaw = netHeadYaw;
        this.headPitch = headPitch;
        this.scale = scale;
    }
}
