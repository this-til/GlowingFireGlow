package com.til.glowing_fire_glow.common.register;

import com.til.glowing_fire_glow.GlowingFireGlow;
import com.til.glowing_fire_glow.common.main.IWorldComponent;
import com.til.glowing_fire_glow.common.util.ReflexUtil;
import com.til.glowing_fire_glow.common.util.ResourceLocationUtil;
import com.til.glowing_fire_glow.common.util.Util;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author til
 */
public abstract class RegisterManage<R extends RegisterBasics> implements IWorldComponent {
    protected Class<R> registerClass;
    protected ResourceLocation registerManageName;
    protected Map<ResourceLocation, R> registerMap;

    protected RegisterManage() {
        registerClass = initType();
        registerManageName = initResourceLocation();
        registerMap = new HashMap<>();
    }

    protected Class<R> initType() {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        Type actualTypeArguments = parameterized.getActualTypeArguments()[0];
        return Util.forcedConversion(ReflexUtil.asClass(actualTypeArguments));
    }

    protected final ResourceLocation initResourceLocation() {
        StringBuilder path = new StringBuilder();
        if (getBasicsRegisterManageClass() != null) {
            path.append(ResourceLocationUtil.ofPath(getBasicsRegisterManageClass()));
            path.append('/');
        }
        path.append(ResourceLocationUtil.ofPath(getClass()));
        return new ResourceLocation(GlowingFireGlow.getInstance().getModIdOfClass(this.getClass()), path.toString());
    }

    public final void put(R register, boolean fromSon) {
        RegisterManage<?> basicsRegisterManage = GlowingFireGlow.getInstance().getReflexManage().getRegisterManage(getBasicsRegisterManageClass());
        if (basicsRegisterManage != null) {
            basicsRegisterManage.put(Util.forcedConversion(register), true);
        }
        registerMap.put(register.getName(), register);
        if (!fromSon) {
            MinecraftForge.EVENT_BUS.register(register);
        }
    }

    public R get(ResourceLocation resourceLocation) {
        return registerMap.get(resourceLocation);
    }

    public Collection<R> forAll() {
        return registerMap.values();
    }

    @Nullable
    public Class<? extends RegisterManage<?>> getBasicsRegisterManageClass() {
        return null;
    }

    public int getVoluntarilyRegisterTime() {
        return 0;
    }


    public Class<R> getRegisterClass() {
        return registerClass;
    }

    public ResourceLocation getRegisterManageName() {
        return registerManageName;
    }

}
