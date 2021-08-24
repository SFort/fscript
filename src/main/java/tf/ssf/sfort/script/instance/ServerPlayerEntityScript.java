package tf.ssf.sfort.script.instance;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import tf.ssf.sfort.script.Default;
import tf.ssf.sfort.script.Help;
import tf.ssf.sfort.script.PredicateProvider;
import tf.ssf.sfort.script.mixin_extended.Config;
import tf.ssf.sfort.script.mixin_extended.ServerPlayerEntityExtended;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ServerPlayerEntityScript<T extends ServerPlayerEntity> implements PredicateProvider<T>, Help {
    public PlayerEntityScript<T> PLAYER_ENTITY = new PlayerEntityScript<>();
    public Predicate<T> getLP(String in, String val){
        return switch (in){
            case "respawn_distance" ->{
                final double arg = Double.parseDouble(val);
                yield player -> {
                    final BlockPos pos = player.getSpawnPointPosition();
                    final ServerWorld world = player.getServerWorld();
                    final RegistryKey<World> dim = player.getSpawnPointDimension();
                    if (pos == null || world == null) return false;
                    return dim.equals(world.getRegistryKey()) && pos.isWithinDistance(player.getPos(), arg);
                };
            }
            case "advancement" -> {
                final Identifier arg = new Identifier(val);
                yield player -> {
                    final MinecraftServer server = player.getServer();
                    if (server == null) return false;
                    return player.getAdvancementTracker().getProgress(server.getAdvancementLoader().get(arg)).isDone();
                };
            }
            default -> null;
        };
    }

    public Predicate<ServerPlayerEntityExtended> getEP(String in){
        return switch (in){
            case "seen_credits" -> ServerPlayerEntityExtended::fscript$seenCredits;
            default -> null;
        };
    }

    //==================================================================================================================

    @Override
    public Predicate<T> getPredicate(String in, String val, Set<Class<?>> dejavu){
        {
            final Predicate<T> out = getLP(in, val);
            if (out != null) return out;
        }
        if (dejavu.add(PlayerEntityScript.class)){
            final Predicate<T> out = PLAYER_ENTITY.getPredicate(in, val, dejavu);
            if (out !=null) return out;
        }
        if (dejavu.add(GameModeScript.class)){
            final Predicate<GameMode> out = Default.GAME_MODE.getPredicate(in, val, dejavu);
            if (out !=null) return player -> out.test(player.interactionManager.getGameMode());
        }
        return null;
    }

    @Override
    public Predicate<T> getPredicate(String in, Set<Class<?>> dejavu){
        if (Config.extended){
            final Predicate<ServerPlayerEntityExtended> out = getEP(in);
            if (out != null) return item -> out.test((ServerPlayerEntityExtended) item);
        }
        if (dejavu.add(PlayerEntityScript.class)){
            final Predicate<T> out = PLAYER_ENTITY.getPredicate(in, dejavu);
            if (out !=null) return out;
        }
        if (dejavu.add(GameModeScript.class)){
            final Predicate<GameMode> out = Default.GAME_MODE.getPredicate(in, dejavu);
            if (out !=null) return player -> out.test(player.interactionManager.getGameMode());
        }
        return null;
    }

    @Override
    public Predicate<T> getEmbed(String in, String script, Set<Class<?>> dejavu){
        //TODO
        if (dejavu.add(PLAYER_ENTITY.getClass()))
            return PLAYER_ENTITY.getEmbed(in, script);
        return null;
    }

    @Override
    public Predicate<T> getEmbed(String in, String val, String script, Set<Class<?>> dejavu){
        //TODO
        if (dejavu.add(PLAYER_ENTITY.getClass()))
            return PLAYER_ENTITY.getEmbed(in, val, script);
        return null;
    }

    //==================================================================================================================

    public static final Map<String, String> help = new HashMap<>();
    static {
        help.put("advancement:AdvancementID","Require advancement unlocked");
        help.put("respawn_distance:double","Require player to be nearby their respawn (usually a bed)");
    }
    @Override
    public Map<String, String> getHelp(){
        return help;
    }
    @Override
    public Map<String, String> getAllHelp(Set<Class<?>> dejavu){
        Stream<Map.Entry<String, String>> out = new HashMap<String, String>().entrySet().stream();
        if (dejavu.add(PlayerEntityScript.class)) out = Stream.concat(out, PLAYER_ENTITY.getAllHelp(dejavu).entrySet().stream());
        if (dejavu.add(GameModeScript.class)) out = Stream.concat(out, Default.GAME_MODE.getAllHelp(dejavu).entrySet().stream());
        out = Stream.concat(out, getHelp().entrySet().stream());

        return out.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
