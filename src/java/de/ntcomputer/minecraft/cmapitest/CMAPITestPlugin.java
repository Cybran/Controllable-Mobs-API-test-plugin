package de.ntcomputer.minecraft.cmapitest;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import de.ntcomputer.minecraft.controllablemobs.api.ControllableMob;
import de.ntcomputer.minecraft.controllablemobs.api.ControllableMobs;
import de.ntcomputer.minecraft.controllablemobs.api.ai.AIType;
import de.ntcomputer.minecraft.controllablemobs.api.ai.behaviors.AIAttackMelee;
import de.ntcomputer.minecraft.controllablemobs.api.ai.behaviors.AITargetNearest;
import de.ntcomputer.minecraft.controllablemobs.api.attributes.AttributeModifier;
import de.ntcomputer.minecraft.controllablemobs.api.attributes.AttributeModifierFactory;
import de.ntcomputer.minecraft.controllablemobs.api.attributes.ModifyOperation;

public final class CMAPITestPlugin extends JavaPlugin {
	private final UUID speedUUID = UUID.fromString("8971a510-ec88-11e2-91e2-0800200c9a66");
	private final AttributeModifier speedModifier = AttributeModifierFactory.create(speedUUID, "speed boost", 2.0, ModifyOperation.MULTIPLY_FINAL_VALUE);
	private final UUID damageUUID = UUID.fromString("e39e3580-ec88-11e2-91e2-0800200c9a66");
	private final AttributeModifier damageModifier = AttributeModifierFactory.create(damageUUID, "damage boost", 10.0, ModifyOperation.ADD_TO_BASIS_VALUE);

	@SuppressWarnings("unchecked")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String className = args[1];
		Class<? extends LivingEntity> entityClass;
		Player player = (Player) sender;
		try {
			entityClass = Class.forName("org.bukkit.entity."+className).asSubclass(LivingEntity.class);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return true;
		}
		
		LivingEntity entity = player.getWorld().spawn(player.getLocation().add(5, 0, 5), entityClass);
		if(entity instanceof Skeleton) {
			Skeleton skeleton = (Skeleton) entity;
			skeleton.getEquipment().setItemInHand(new ItemStack(Material.BOW,1));
		}
		ControllableMob<?> controllableEntity = ControllableMobs.putUnderControl(entity);
		
		if(args[0].equalsIgnoreCase("move1")) {
			controllableEntity.getAI().clear();
			controllableEntity.getActions().moveTo(controllableEntity.getEntity().getLocation().add(20,0,0));
			controllableEntity.getActions().die(true);
		}
		
		if(args[0].equalsIgnoreCase("follow")) {
			//controllableEntity.getAI().clear();
			controllableEntity.getActions().follow(player);
		}
		
		if(args[0].equalsIgnoreCase("speed")) {
			controllableEntity.getAttributes().getMovementSpeedAttribute().attachModifier(this.speedModifier);
		}
		
		if(args[0].equalsIgnoreCase("damageamp")) {
			controllableEntity.getAttributes().getAttackDamageAttribute().attachModifier(this.damageModifier);
		}
		
		if(args[0].equalsIgnoreCase("move2")) {
			controllableEntity.getActions().moveTo(controllableEntity.getEntity().getLocation().add(20,0,0));
			controllableEntity.getActions().wait(60, true);
			controllableEntity.getActions().moveTo(controllableEntity.getEntity().getLocation(), true);
			controllableEntity.getActions().die(true);
		}
		
		if(args[0].equalsIgnoreCase("chickenslayer")) {
			controllableEntity.getAI().removeExcept(AIType.ATTACK_MELEE, AIType.ATTACK_RANGED, AIType.ATTACK_SWELL);
			if(controllableEntity.getAI().hasBehavior(AIType.ATTACK_MELEE)) {
				((ControllableMob<? extends Creature>) controllableEntity).getAI().addBehavior(new AIAttackMelee());
			}
			controllableEntity.getAI().addBehavior(new AITargetNearest(0,32,true,60,null,Chicken.class));
			controllableEntity.getAttributes().getMovementSpeedAttribute().attachModifier(speedModifier);
		}
		
		if(args[0].equalsIgnoreCase("look1")) {
			controllableEntity.getAI().clear();
			controllableEntity.getActions().lookAt(player);
		}
		
		if(args[0].equalsIgnoreCase("look2")) {
			controllableEntity.getAI().clear();
			controllableEntity.getActions().lookAt(player.getLocation());
		}
		
		if(args[0].equalsIgnoreCase("knockback")) {
			controllableEntity.getAI().clear();
			controllableEntity.getAttributes().getKnockbackResistanceAttribute().setBasisValue(1.0);
		}
		
		if(args[0].equalsIgnoreCase("monster")) {
			controllableEntity.getAttributes().getAttackDamageAttribute().attachModifier(damageModifier);
			controllableEntity.getAttributes().getMaxHealthAttribute().setBasisValue(100.0);
			controllableEntity.getEntity().setHealth(controllableEntity.getAttributes().getMaxHealthAttribute().getValue());
		}
		
		if(args[0].equalsIgnoreCase("jump")) {
			controllableEntity.getActions().jump(10);
		}
		
		return false;
	}

}
