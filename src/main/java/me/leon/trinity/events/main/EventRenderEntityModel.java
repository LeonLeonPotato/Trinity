package me.leon.trinity.events.main;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.TrinityEvent;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;

public class EventRenderEntityModel extends TrinityEvent {
    public ModelBase modelBase;
    public Entity entityIn;
    public float limbSwing;
    public float limbSwingAmount;
    public float ageInTicks;
    public float netHeadYaw;
    public float headPitch;
    public float scale;

    public EventRenderEntityModel(EventStage stage, ModelBase modelBase, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        super(stage);
        this.modelBase = modelBase;
        this.entityIn = entityIn;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.ageInTicks = ageInTicks;
        this.netHeadYaw = netHeadYaw;
        this.headPitch = headPitch;
        this.scale = scale;
    }
}
