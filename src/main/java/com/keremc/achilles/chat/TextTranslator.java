package com.keremc.achilles.chat;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.core.util.ClassUtils;
import net.md_5.bungee.api.chat.TextComponent;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

public abstract class TextTranslator<T> {
    private static final Map<Class, TextTranslator> TRANSLATORS = new HashMap<>();

    static {
        try {
            for (Class clazz : ClassUtils.getClassesInPackage(AchillesPlugin.getInstance(), "com.keremc.achilles.chat.defaults")) {
                if (TextTranslator.class.isAssignableFrom(clazz)) {
                    TextTranslator trans = (TextTranslator) clazz.newInstance();

                    register(trans);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public static void register(TextTranslator translator) {
        Class type = (Class) ((ParameterizedType) translator.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        TRANSLATORS.put(type, translator);
    }

    public static TextComponent translate(Object object, ChatFlags flags) {

        TextTranslator translator = getTranslator(object.getClass());

        if (translator != null) {
            return translator.format(object, flags);
        }

        return new TextComponent(object.toString());
    }

    public static TextTranslator getTranslator(Class type) {

        if (TRANSLATORS.containsKey(type)) {
            return TRANSLATORS.get(type);
        } else {
            for (Map.Entry<Class, TextTranslator> entry : TRANSLATORS.entrySet()) {
                if (entry.getKey().isAssignableFrom(type)) {
                    return entry.getValue();
                }
            }
        }

        return null;
    }

    /**
     * Returns the Minecraft JSON chat equivalent of a POJO
     *
     * @param object object to serialize into chat
     * @return Bungee Chat API Chat JSON object
     */
    public abstract TextComponent format(T object, ChatFlags flags);

}
