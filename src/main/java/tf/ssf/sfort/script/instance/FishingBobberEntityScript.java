package tf.ssf.sfort.script.instance;

import net.minecraft.entity.projectile.FishingBobberEntity;
import tf.ssf.sfort.script.Default;
import tf.ssf.sfort.script.Help;
import tf.ssf.sfort.script.PredicateProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FishingBobberEntityScript<T extends FishingBobberEntity> implements PredicateProvider<T>, Help {
    private final EntityScript<T> ENTITY = new EntityScript<>();
    public Predicate<T> getLP(String in){
        return switch (in){
            case "is_bobber_in_open_water" -> FishingBobberEntity::isInOpenWater;
            default -> null;
        };
    }
    public Predicate<T> getLP(String in, String val){
        return null;
    }
    @Override
    public Predicate<T> getPredicate(String in, String val, Set<Class<?>> dejavu){
        {
            Predicate<T> out = getLP(in, val);
            if (out != null) return out;
        }
        if (dejavu.add(EntityScript.class)){
            Predicate<T> out = ENTITY.getPredicate(in, val, dejavu);
            if (out !=null) return out;
        }
        return null;
    }

    @Override
    public Predicate<T> getPredicate(String in, Set<Class<?>> dejavu){
        {
            Predicate<T> out = getLP(in);
            if (out != null) return out;
        }
        if (dejavu.add(EntityScript.class)){
            Predicate<T> out = ENTITY.getPredicate(in, dejavu);
            if (out !=null) return out;
        }
        return null;
    }
    public static final Map<String, String> help = new HashMap<>();
    static {
        help.put("is_bobber_in_open_water","Require a fishing bobber in open water");
    }
    @Override
    public Map<String, String> getHelp(){
        return help;
    }
    @Override
    public Map<String, String> getAllHelp(Set<Class<?>> dejavu){
        Stream<Map.Entry<String, String>> out = new HashMap<String, String>().entrySet().stream();
        if (dejavu.add(EntityScript.class)) out = Stream.concat(out, Default.ENTITY.getAllHelp(dejavu).entrySet().stream());
        out = Stream.concat(out, getHelp().entrySet().stream());

        return out.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
