package com.kamikazejam.kamicommon.nms.command;

import com.kamikazejam.kamicommon.nms.abstraction.command.CommandMapModifier;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Map;

public class CommandMapModifier_1_8_R1 implements CommandMapModifier {
    private final SimpleCommandMap simpleCommandMap;
    private final Map<String, Command> knownCommands;
    public CommandMapModifier_1_8_R1() {
        simpleCommandMap = getSimpleCommandMap();
        knownCommands = getSimpleCommandMapDotKnownCommands(simpleCommandMap);
    }

    @Override
    public void unregisterCommand(@NotNull Command command) {
        command.unregister(simpleCommandMap);
    }

    @Override
    public void registerCommand(@NotNull Command command, @NotNull Plugin plugin) {
        simpleCommandMap.register(plugin.getName(), command);
    }

    @Override
    public @NotNull Map<String, Command> getKnownCommands() {
        return knownCommands;
    }

    // -------------------------------------------- //
    // GETTERS
    // -------------------------------------------- //

    protected static Field SERVER_DOT_COMMAND_MAP = getField(Bukkit.getServer().getClass(), "commandMap");
    public static SimpleCommandMap getSimpleCommandMap() {
        Server server = Bukkit.getServer();
        return getField(SERVER_DOT_COMMAND_MAP, server);
    }

    protected static Field SIMPLE_COMMAND_MAP_DOT_KNOWN_COMMANDS = getField(SimpleCommandMap.class, "knownCommands");
    public static Map<String, Command> getSimpleCommandMapDotKnownCommands(SimpleCommandMap simpleCommandMap) {
        return getField(SIMPLE_COMMAND_MAP_DOT_KNOWN_COMMANDS, simpleCommandMap);
    }



    // -------------------------------------------- //
    // REFLECTION: FIELD > GET
    // -------------------------------------------- //
    @Contract("null, _ -> fail; !null, null -> fail")
    public static @NotNull Field getField(Class<?> clazz, String name) {
        if (clazz == null) throw new NullPointerException("clazz");
        if (name == null) throw new NullPointerException("name");
        try {
            Field ret = clazz.getDeclaredField(name);
            makeAccessible(ret);
            return ret;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @SuppressWarnings("unchecked")
    public static <T> T getField(@NotNull Field field, Object object) {
        try {
            return (T) field.get(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // -------------------------------------------- //
    // REFLECTION: MAKE ACCESSIBLE
    // -------------------------------------------- //
    @Contract(value = "null -> fail")
    public static void makeAccessible(Field field) {
        try {
            // Mark as accessible using reflection.
            field.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
