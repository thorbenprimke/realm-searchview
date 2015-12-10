package co.moonmonkeylabs.realmsearchview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * A custom adapter for the {@link RealmSearchView}. It has options to customize the filtering.
 */
public abstract class RealmSearchAdapter<T extends RealmObject, VH extends RealmSearchViewHolder>
        extends RealmBasedRecyclerViewAdapter<T, VH> {

    private Realm realm;
    protected Class<T> clazz;

    private String filterKey;

    private boolean useContains;
    private Case casing;
    private Sort sortOrder;
    private String sortKey;
    private String basePredicate;

    /**
     * Creates a {@link RealmSearchAdapter} with only the filter columnKey. The defaults are:
     * - useContains: true
     * - casing: insensitive
     * - sortOrder: ascending
     * - sortKey: filterKey
     * - basePredicate: not set
     */
    public RealmSearchAdapter(
            @NonNull Context context,
            @NonNull Realm realm,
            @NonNull String filterKey) {
        this(context, realm, filterKey, true, Case.INSENSITIVE, Sort.ASCENDING, filterKey, null);
    }

    /**
     * Creates a {@link RealmSearchAdapter} with parameters for all options.
     */
    @SuppressWarnings("unchecked")
    public RealmSearchAdapter(
            @NonNull Context context,
            @NonNull Realm realm,
            @NonNull String filterKey,
            boolean useContains,
            Case casing,
            Sort sortOrder,
            String sortKey,
            String basePredicate) {
        super(context, null, false, false);
        this.realm = realm;
        this.filterKey = filterKey;
        this.useContains = useContains;
        this.casing = casing;
        this.sortOrder = sortOrder;
        this.sortKey = sortKey;
        this.basePredicate = basePredicate;

        clazz = (Class<T>) getTypeArguments(RealmSearchAdapter.class, getClass()).get(0);
    }

    @Override
    public void onBindFooterViewHolder(VH holder, int position) {
        holder.footerTextView.setText("I'm a footer");
    }

    @Override
    @SuppressWarnings("unchecked")
    public VH onCreateFooterViewHolder(ViewGroup viewGroup) {
        View v = inflater.inflate(R.layout.footer_view, viewGroup, false);
        RealmSearchViewHolder vh = new RealmSearchViewHolder(
                (FrameLayout) v,
                (TextView) v.findViewById(R.id.footer_text_view));
        return (VH) vh;
    }

    public void filter(String input) {
        RealmResults<T> businesses;
        RealmQuery<T> where = realm.where(clazz);
        if (input.isEmpty() && basePredicate != null) {
            if (useContains) {
                where = where.contains(filterKey, basePredicate, casing);
            } else {
                where = where.beginsWith(filterKey, basePredicate, casing);
            }
        } else if (!input.isEmpty()) {
            if (useContains) {
                where = where.contains(filterKey, input, casing);
            } else {
                where = where.beginsWith(filterKey, input, casing);
            }
        }

        if (sortKey == null) {
            businesses = where.findAll();
        } else {
            businesses = where.findAllSorted(sortKey, sortOrder);
        }
        updateRealmResults(businesses);
    }

    /**
     * The columnKey by which the results are filtered.
     */
    public void setFilterKey(String filterKey) {
        if (filterKey == null) {
            throw new IllegalStateException("The filterKey cannot be null.");
        }
        this.filterKey = filterKey;
    }

    /**
     * If true, {@link RealmQuery#contains} is used else {@link RealmQuery#beginsWith}.
     */
    public void setUseContains(boolean useContains) {
        this.useContains = useContains;
    }

    /**
     * Sets if the filtering is case sensitive or case insensitive.
     */
    public void setCasing(Case casing) {
        this.casing = casing;
    }

    /**
     * Sets if the sort order is ascending or descending.
     */
    public void setSortOrder(Sort sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * Sets the sort columnKey.
     */
    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }

    /**
     * Sets the basePredicate which is used filters the results when the search query is empty.
     */
    public void setBasePredicate(String basePredicate) {
        this.basePredicate = basePredicate;
    }

    //
    // The code below is copied from StackOverflow in order to avoid having to pass in the T as a
    // Class for the Realm query/filtering.
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
