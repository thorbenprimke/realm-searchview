package co.moonmonkeylabs.realmsearchview;

import android.content.Context;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;

/**
 * Created by thorben on 11/1/15.
 */
public abstract class RealmSearchAdapter<T extends RealmObject, VH extends RealmViewHolder>
        extends RealmBasedRecyclerViewAdapter<T, VH> {

    private Realm realm;
    private String filterColumnName;
    protected Class<T> clazz;


    public RealmSearchAdapter(
            Context context,
            Realm realm,
            String filterColumnName) {
        super(context, null, false, false);
        this.realm = realm;
        this.filterColumnName = filterColumnName;

        clazz = (Class<T>) getTypeArguments(RealmSearchAdapter.class, getClass()).get(0);
    }

    public void filter(String input) {
        RealmResults<T> businesses;
        if (input.isEmpty()) {
            businesses = realm.where(clazz).findAllSorted(filterColumnName, true);
        } else {
            businesses = realm.where(clazz)
                    .contains(filterColumnName, input, false)
                    .findAllSorted(filterColumnName, true);
        }
        updateRealmResults(businesses);
    }


    //
    // The code below is copied from StackOverflow in order to avoid having to pass in the T as a
    // Class for the Realm filtering.
    // http://stackoverflow.com/a/15008017
    //
    /**
     * Get the underlying class for a type, or null if the type is a variable
     * type.
     *
     * @param type the type
     * @return the underlying class
     */
    public static Class<?> getClass(Type type)
    {
        if (type instanceof Class) {
            return (Class) type;
        } else if (type instanceof ParameterizedType) {
            return getClass(((ParameterizedType) type).getRawType());
        } else if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            Class<?> componentClass = getClass(componentType);
            if (componentClass != null) {
                return Array.newInstance(componentClass, 0).getClass();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Get the actual type arguments a child class has used to extend a generic
     * base class.
     *
     * @param baseClass the base class
     * @param childClass the child class
     * @return a list of the raw classes for the actual type arguments.
     */
    public static <T> List<Class<?>> getTypeArguments(
            Class<T> baseClass, Class<? extends T> childClass)
    {
        Map<Type, Type> resolvedTypes = new HashMap<Type, Type>();
        Type type = childClass;
        // start walking up the inheritance hierarchy until we hit baseClass
        while (!getClass(type).equals(baseClass)) {
            if (type instanceof Class) {
                // there is no useful information for us in raw types, so just keep going.
                type = ((Class) type).getGenericSuperclass();
            } else {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Class<?> rawType = (Class) parameterizedType.getRawType();

                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
                for (int i = 0; i < actualTypeArguments.length; i++) {
                    resolvedTypes.put(typeParameters[i], actualTypeArguments[i]);
                }

                if (!rawType.equals(baseClass)) {
                    type = rawType.getGenericSuperclass();
                }
            }
        }

        // finally, for each actual type argument provided to baseClass, determine (if possible)
        // the raw class for that type argument.
        Type[] actualTypeArguments;
        if (type instanceof Class) {
            actualTypeArguments = ((Class) type).getTypeParameters();
        } else {
            actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
        }
        List<Class<?>> typeArgumentsAsClasses = new ArrayList<Class<?>>();
        // resolve types by chasing down type variables.
        for (Type baseType : actualTypeArguments) {
            while (resolvedTypes.containsKey(baseType)) {
                baseType = resolvedTypes.get(baseType);
            }
            typeArgumentsAsClasses.add(getClass(baseType));
        }
        return typeArgumentsAsClasses;
    }
    //
    // End StackOverflow code
    //
}
