package me.leon.trinity.utils.world.Rotation;

import me.leon.trinity.utils.Util;
import me.leon.trinity.utils.world.Priority;
import me.leon.trinity.utils.world.Timer;

public class Rotation implements Util {
	public float pitch, yaw;
	public boolean packet, stay;
	public Timer rotationStay = new Timer();
	public int time;
	public Priority priority;

	public Rotation(float pitch, float yaw) {
		this.pitch = pitch;
		this.yaw = yaw;
		this.packet = true;
		this.stay = true;
		this.priority = Priority.Normal;

		rotationStay.reset();
	}

	public Rotation(float pitch, float yaw, boolean packet) {
		this.pitch = pitch;
		this.yaw = yaw;
		this.packet = packet;
		this.stay = true;
		this.priority = Priority.Normal;

		rotationStay.reset();
	}

	public Rotation(float pitch, float yaw, boolean packet, Priority priority) {
		this.pitch = pitch;
		this.yaw = yaw;
		this.packet = packet;
		this.stay = true;
		this.priority = priority;

		rotationStay.reset();
	}

	public Rotation(float pitch, float yaw, boolean packet, boolean stay, int time, Priority priority) {
		this.pitch = pitch;
		this.yaw = yaw;
		this.packet = packet;
		this.stay = stay;
		this.time = time;
		this.priority = priority;

		rotationStay.reset();
	}

	// updates player rotations here
	public void updateRotations() {
		try {
			if (this.packet) {
				mc.player.renderYawOffset = this.yaw;
				mc.player.rotationYawHead = this.yaw;
			} else {
				mc.player.rotationYaw = this.yaw;
				mc.player.rotationPitch = this.pitch;
			}
		} catch (Exception ignored) {

		}
	}

	public void cancel() {
		this.yaw = mc.player.rotationYaw;
		this.pitch = mc.player.rotationPitch;

		this.rotationStay.reset();
	}
}
