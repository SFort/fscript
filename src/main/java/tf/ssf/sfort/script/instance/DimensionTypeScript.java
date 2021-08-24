package tf.ssf.sfort.script.instance;

import net.minecraft.world.dimension.DimensionType;
import tf.ssf.sfort.script.Help;
import tf.ssf.sfort.script.PredicateProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class DimensionTypeScript implements PredicateProvider<DimensionType>, Help {

    public Predicate<DimensionType> getLP(String in){
        return switch (in){
            case "natural" -> DimensionType::isNatural;
            case "ultrawarn" -> DimensionType::isUltrawarm;
            case "piglin_safe" -> DimensionType::isPiglinSafe;
            case "does_bed_work" -> DimensionType::isBedWorking;
            case "does_anchor_work" -> DimensionType::isRespawnAnchorWorking;
            case "has_skylight" -> DimensionType::hasSkyLight;
            case "has_ceiling" -> DimensionType::hasCeiling;
            case "has_raids" -> DimensionType::hasRaids;
            case "has_ender_dragon_fight" -> DimensionType::hasEnderDragonFight;
            case "has_fixed_time" -> DimensionType::hasFixedTime;
            default -> null;
        };
    }
    public Predicate<DimensionType> getLP(String in, String val){
        return switch (in){
            case "coordinate_scale" -> {
                final double arg = Double.parseDouble(val);
                yield dim -> dim.getCoordinateScale() >= arg;
            }
            default -> null;
        };
    }
    //==================================================================================================================

    @Override
    public Predicate<DimensionType> getPredicate(String in, Set<Class<?>> dejavu){
        return getLP(in);
    }
    @Override
    public Predicate<DimensionType> getPredicate(String in, String val, Set<Class<?>> dejavu){
        return getLP(in, val);
    }


    //==================================================================================================================

    public static final Map<String, String> help = new HashMap<>();
    static {
        help.put("dim_natural","Require natural dimension");
        help.put("dim_ultrawarn","Require ultra warm dimension");
        help.put("dim_piglin_safe","Require piglin safe dimension");
        help.put("dim_does_bed_work","Require dimension where beds don't blow");
        help.put("dim_does_anchor_work","Require dimension where respawn anchors work");
    }
    @Override
    public Map<String, String> getHelp(){
        return help;
    }
    @Override
    public Map<String, String> getAllHelp(Set<Class<?>> dejavu){
        return getHelp();
    }
}