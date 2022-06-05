package com.github.alfonsoleandro.corona.managers;

import com.github.alfonsoleandro.corona.Corona;
import com.github.alfonsoleandro.mputils.reloadable.Reloadable;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.*;

public class RecipesManager extends Reloadable {

    private final Corona plugin;
    private final Settings settings;
    private final NamespacedKey potionRecipeKey;
    private final NamespacedKey maskRecipeKey;

    public RecipesManager(Corona plugin) {
        super(plugin);
        this.plugin = plugin;
        this.settings = plugin.getSettings();
        this.potionRecipeKey = new NamespacedKey(plugin, "CurePotion");
        this.maskRecipeKey = new NamespacedKey(plugin, "FaceMask");
        registerMaskRecipe();
        registerPotionRecipe();
    }

    public void registerPotionRecipe() {
        if(!this.settings.isCurePotionEnabled() || !this.settings.isCurePotionRecipeEnabled()) {
            return;
        }
        FileConfiguration config = this.plugin.getConfigYaml().getAccess();

        String path = "config.cure potion";
        ItemStack potion = new ItemStack(Material.POTION);
        ItemMeta meta = potion.getItemMeta();
        assert meta != null;
        ((PotionMeta)meta).addCustomEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1200, 4), true);
        ((PotionMeta)meta).addCustomEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1200, 4), true);

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString(path + ".name"))));
        List<String> lore = new ArrayList<>();
        for (String linea : config.getStringList(path+".lore")) {
            lore.add(ChatColor.translateAlternateColorCodes('&', linea));
        }
        meta.setLore(lore);
        potion.setItemMeta(meta);

        ShapedRecipe potionRecipe = new ShapedRecipe(this.potionRecipeKey, potion);
        potionRecipe.shape("ABC","DEF","GHI");
        potionRecipe.setIngredient('A', Material.valueOf(config.getString(path+".recipe.A")));
        potionRecipe.setIngredient('B', Material.valueOf(config.getString(path+".recipe.B")));
        potionRecipe.setIngredient('C', Material.valueOf(config.getString(path+".recipe.C")));
        potionRecipe.setIngredient('D', Material.valueOf(config.getString(path+".recipe.D")));
        potionRecipe.setIngredient('E', Material.valueOf(config.getString(path+".recipe.E")));
        potionRecipe.setIngredient('F', Material.valueOf(config.getString(path+".recipe.F")));
        potionRecipe.setIngredient('G', Material.valueOf(config.getString(path+".recipe.G")));
        potionRecipe.setIngredient('H', Material.valueOf(config.getString(path+".recipe.H")));
        potionRecipe.setIngredient('I', Material.valueOf(config.getString(path+".recipe.I")));

        Bukkit.addRecipe(potionRecipe);
    }



    public void registerMaskRecipe() {
        if(!this.settings.isMaskEnabled() || !this.settings.isMaskRecipeEnabled()) {
            return;
        }
        FileConfiguration config = this.plugin.getConfigYaml().getAccess();
        String path = "config.mask";
        String playerSkullTextureURL = config.getString(path+".texture URL");
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skull = (SkullMeta) head.getItemMeta();

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", playerSkullTextureURL).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField;
        try {
            assert skull != null;
            profileField = skull.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skull, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
            e1.printStackTrace();
        }

        skull.setDisplayName(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString(path + ".name"))));
        List<String> lore = new ArrayList<>();
        for (String linea : config.getStringList(path+".lore")) {
            lore.add(ChatColor.translateAlternateColorCodes('&', linea));
        }
        skull.setLore(lore);
        head.setItemMeta(skull);
        head.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 10);

        ShapedRecipe maskRecipe = new ShapedRecipe(this.maskRecipeKey, head);
        maskRecipe.shape("ABC","DEF","GHI");
        maskRecipe.setIngredient('A', Material.valueOf(config.getString(path+".recipe.A")));
        maskRecipe.setIngredient('B', Material.valueOf(config.getString(path+".recipe.B")));
        maskRecipe.setIngredient('C', Material.valueOf(config.getString(path+".recipe.C")));
        maskRecipe.setIngredient('D', Material.valueOf(config.getString(path+".recipe.D")));
        maskRecipe.setIngredient('E', Material.valueOf(config.getString(path+".recipe.E")));
        maskRecipe.setIngredient('F', Material.valueOf(config.getString(path+".recipe.F")));
        maskRecipe.setIngredient('G', Material.valueOf(config.getString(path+".recipe.G")));
        maskRecipe.setIngredient('H', Material.valueOf(config.getString(path+".recipe.H")));
        maskRecipe.setIngredient('I', Material.valueOf(config.getString(path+".recipe.I")));

        Bukkit.addRecipe(maskRecipe);
    }


    public void unregisterRecipes(){
        Bukkit.getServer().removeRecipe(this.potionRecipeKey);
        Bukkit.getServer().removeRecipe(this.maskRecipeKey);
    }




    @Override
    public void reload(boolean deep) {
        unregisterRecipes();
        registerMaskRecipe();
        registerPotionRecipe();
    }
}
