/**
 * Copyright (c) 2009 Ed Merks and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   Ed Merks - Initial API and implementation
 */
package org.eclipse.emf.ecore.impl;


import java.util.Arrays;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.common.util.ArrayDelegatingEList;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EContentsEList;
import org.eclipse.emf.ecore.util.ECrossReferenceEList;


/**
 * A space compact implementation of the model object '<em><b>EObject</b></em>'.
 * @since 2.5
 */
public class MinimalEObjectImpl extends BasicEObjectImpl implements EStructuralFeature.Internal.DynamicValueHolder
{
  /**
   * A space compact implementation of the model object '<em><b>EObject</b></em>' that is typically contained by another EObject.
   * It provides the {@link #eContainer} field which is used by {@link #eInternalContainer()} and {@link #eBasicSetContainer(InternalEObject)}.
   */
  public static class Container extends MinimalEObjectImpl
  {
    /**
     * A space compact implementation of the model object '<em><b>EObject</b></em>' that is typically used as a dynamic base.
     * It provides the {@link #eClass} field which is used by {@link #eClass()}, {@link #eDynamicClass()}, and {@link #eSetClass(EClass)}.
     * It also provides the {@link #eSettings} field which is used by {@link #eHasSettings()}, {@link #eBasicSettings()}, and {@link #eBasicSetSettings(Object[])}.
     */
    public static class Dynamic extends Container
    {
      /**
       * A space compact implementation of the model object '<em><b>EObject</b></em>' that is typically used as a dynamic base
       * where the dynamic EClass's {@link EClass#getEAllStructuralFeatures() features} can be modified via additions and removals at runtime, including the addition or removal of super types.
       * It provides the {@link #eAllStructuralFeatures} field to {@link #eSettings() record} the features for which there are currently {@link #eSettings} available.
       * This field is used to {@link #eBasicSettings() reconcile} the settings when they are accessed after a change to the EClass's features.
       *
       * @since 2.33
       */
      public static class Permissive extends Dynamic
      {
        protected EList<EStructuralFeature> eAllStructuralFeatures;

        public Permissive()
        {
          super();
        }

        public Permissive(EClass eClass)
        {
          super(eClass);
        }

        @Override
        protected Object[] eBasicSettings()
        {
          if (eAllStructuralFeatures != null)
          {
            EList<EStructuralFeature> newEAllStructuralFeatures = eClass.getEAllStructuralFeatures();
            if (newEAllStructuralFeatures != eAllStructuralFeatures)
            {
              int eStaticFeatureCount = eStaticFeatureCount();
              int newSize = newEAllStructuralFeatures.size() - eStaticFeatureCount;
              Object[] newESettings;
              if (newSize == 0)
              {
                newESettings = null;
              }
              else
              {
                newESettings = new Object [newSize];
                if (eSettings != null)
                {
                  for (int i = 0, oldSize = eAllStructuralFeatures.size() - eStaticFeatureCount; i < oldSize; ++i)
                  {
                    EStructuralFeature eStructuralFeature = eAllStructuralFeatures.get(i + eStaticFeatureCount);
                    int index = newEAllStructuralFeatures.indexOf(eStructuralFeature);
                    if (index != -1)
                    {
                      newESettings[index - eStaticFeatureCount] = eSettings[i];
                    }
                  }
                }
              }

              eSettings = newESettings;
              eAllStructuralFeatures = newEAllStructuralFeatures;
            }
          }

          return eSettings;
        }

        @Override
        protected EStructuralFeature.Internal.DynamicValueHolder eSettings()
        {
          if (eSettings == null && eAllStructuralFeatures == null)
          {
            eAllStructuralFeatures = eClass.getEAllStructuralFeatures();
            int size = eAllStructuralFeatures.size() - eStaticFeatureCount();
            if (size != 0)
            {
              eSettings = new Object [size];
            }
          }

          return this;
        }
      }

      public static final class BasicEMapEntry<K, V> extends Dynamic implements BasicEMap.Entry<K, V>
      {
        protected int hash = -1;

        protected EStructuralFeature keyFeature;

        protected EStructuralFeature valueFeature;

        /**
         * Creates a dynamic EObject.
         */
        public BasicEMapEntry()
        {
          super();
        }

        /**
         * Creates a dynamic EObject.
         */
        public BasicEMapEntry(EClass eClass)
        {
          super(eClass);
        }

        @SuppressWarnings("unchecked")
        public K getKey()
        {
          return (K)eGet(keyFeature);
        }

        public void setKey(Object key)
        {
          eSet(keyFeature, key);
        }

        public int getHash()
        {
          if (hash == -1)
          {
            Object theKey = getKey();
            hash = (theKey == null ? 0 : theKey.hashCode());
          }
          return hash;
        }

        public void setHash(int hash)
        {
          this.hash = hash;
        }

        @SuppressWarnings("unchecked")
        public V getValue()
        {
          return (V)eGet(valueFeature);
        }

        public V setValue(V value)
        {
          @SuppressWarnings("unchecked")
          V result = (V)eGet(valueFeature);
          eSet(valueFeature, value);
          return result;
        }

        @Override
        public void eSetClass(EClass eClass)
        {
          super.eSetClass(eClass);
          keyFeature = eClass.getEStructuralFeature("key");
          valueFeature = eClass.getEStructuralFeature("value");
        }
      }

      protected EClass eClass;

      protected Object[] eSettings;

      public Dynamic()
      {
        super();
      }

      public Dynamic(EClass eClass)
      {
        super();
        eSetClass(eClass);
      }

      @Override
      public EClass eClass()
      {
        return eClass;
      }

      @Override
      protected EClass eDynamicClass()
      {
        return eClass();
      }

      @Override
      public void eSetClass(EClass eClass)
      {
        this.eClass = eClass;
      }

      @Override
      protected boolean eHasSettings()
      {
        return eSettings != null;
      }

      @Override
      protected Object[] eBasicSettings()
      {
        return eSettings;
      }

      @Override
      protected void eBasicSetSettings(Object[] settings)
      {
        this.eSettings = settings;
      }
    }

    protected InternalEObject eContainer;

    public Container()
    {
      super();
    }

    @Override
    public InternalEObject eInternalContainer()
    {
      return eContainer;
    }

    @Override
    protected void eBasicSetContainer(InternalEObject newContainer)
    {
      eContainer = newContainer;
    }
  }

  /**
   * The {@link #eFlags bit flag} for {@link #eDeliver()}.
   */
  private static final int NO_DELIVER = 1 << 0;

  /**
   * The {@link #eFlags bit flag} for indicating that a dynamic {@link #eContainer() container} field is allocated.
   * A derived implementation wishing to allocate a static container field
   * should override {@link #eInternalContainer()} and {@link #eBasicSetContainer(InternalEObject)}.
   */
  private static final int CONTAINER = 1 << 1;

  /**
   * The {@link #eFlags bit flag} for indicating that a dynamic {@link #eAdapters() adapters} field is allocated.
   * A derived implementation wishing to allocate a static adapter field
   * should override {@link #eBasicHasAdapters()}, {@link #eBasicAdapterArray()}, and {@link #eBasicSetAdapterArray(Adapter[])}.
   */
  private static final int ADAPTER = 1 << 2;

  /**
   * The {@link #eFlags bit flag} for indicating that a dynamic {@link org.eclipse.emf.common.notify.impl.BasicNotifierImpl.EObservableAdapterList.Listener adapter list listener} field is allocated.
   */
  private static final int ADAPTER_LISTENER = 1 << 3;

  /**
   * The {@link #eFlags bit flag} for indicating that a dynamic {@link #eClass() class} field is allocated.
   * A derived implementation wishing to allocate a static class field
   * should override {@link #eDynamicClass()} and {@link #eSetClass(EClass)}.
   */
  private static final int CLASS = 1 << 4;

  /**
   * The {@link #eFlags bit flag} for indicating that a dynamic {@link #eSettings() settings} field is allocated.
   * A derived implementation wishing to allocate a static settings field
   * should override {@link #eHasSettings()}, {@link #eBasicSettings()}, and {@link #eBasicSetSettings(Object[])}.
   */
  private static final int SETTING = 1 << 5;

  /**
   * The {@link #eFlags bit flag} for indicating that a dynamic {@link #eProxyURI() proxy URI} field is allocated.
   * A derived implementation wishing to allocate a static proxy URI field
   * should override {@link #eIsProxy()}, {@link #eProxyURI()}, and {@link #eSetProxyURI(URI)}.
   */
  private static final int PROXY = 1 << 6;

  /**
   * The {@link #eFlags bit flag} for indicating that a dynamic {@link #eResource() resource} field is allocated.
   * A derived implementation wishing to allocate a static resource field
   * should override {@link #eDirectResource()} and {@link #eSetDirectResource(Resource.Internal)}.
   */
  private static final int RESOURCE = 1 << 7;

  /**
   * A bit mask for all the bit flags representing fields.
   */
  private static final int FIELD_MASK = CONTAINER | ADAPTER | ADAPTER_LISTENER | CLASS | SETTING | PROXY | RESOURCE;

  /**
   * A bit flag field with bits for
   * {@link #NO_DELIVER},
   * {@link #CONTAINER},
   * {@link #ADAPTER},
   * {@link #ADAPTER_LISTENER},
   * {@link #CLASS},
   * {@link #SETTING},
   * {@link #PROXY},
   * and
   * {@link #RESOURCE}.
   * The high order 16 bits are used to represent the {@link #eContainerFeatureID() container feature ID},
   * a derived implementation wishing to allocate a static container feature ID field
   * should override {@link #eContainerFeatureID()} and {@link #eBasicSetContainerFeatureID(int)}.
   * @see #NO_DELIVER
   * @see #CONTAINER
   * @see #ADAPTER
   * @see #ADAPTER_LISTENER
   * @see #CLASS
   * @see #SETTING
   * @see #PROXY
   * @see #RESOURCE
   */
  private int eFlags;

  /**
   * The storage location for dynamic fields.
   */
  private Object eStorage;

  /**
   * Creates a minimal EObject.
   */
  protected MinimalEObjectImpl()
  {
    super();
  }

  @Override
  protected EPropertiesHolder eProperties()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  protected EPropertiesHolder eBasicProperties()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  protected BasicEList<Adapter> eBasicAdapters()
  {
    throw new UnsupportedOperationException();
  }

  private final boolean hasField(int field)
  {
    return (eFlags & field) != 0;
  }

  private final Object getField(int field)
  {
    if (hasField(field))
    {
      int fieldIndex = fieldIndex(field);
      return fieldIndex == -1 ? eStorage : ((Object[])eStorage)[fieldIndex];
    }
    else
    {
      return null;
    }
  }

  private final void setField(int field, Object value)
  {
    if (hasField(field))
    {
      if (value == null)
      {
        removeField(field);
      }
      else
      {
        int fieldIndex = fieldIndex(field);
        if (fieldIndex == -1)
        {
          eStorage = value;
        }
        else
        {
          ((Object[])eStorage)[fieldIndex] = value;
        }
      }
    }
    else if (value != null)
    {
      addField(field, value);
    }
  }

  private final int fieldIndex(int field)
  {
    int result = 0;
    for (int bit = CONTAINER; bit < field; bit <<= 1)
    {
      if ((eFlags & bit) != 0)
      {
        ++result;
      }
    }
    if (result == 0)
    {
      for (int bit = field <<= 1; bit <= RESOURCE; bit <<= 1)
      {
        if ((eFlags & bit) != 0)
        {
          return 0;
        }
      }
      return -1;
    }
    else
    {
      return result;
    }
  }

  private final void addField(int field, Object value)
  {
    int fieldCount = Integer.bitCount(eFlags & FIELD_MASK);
    if (fieldCount == 0)
    {
      eStorage = value;
    }
    else
    {
      Object[] result;
      if (fieldCount == 1)
      {
        result = new Object [2];
        int fieldIndex = fieldIndex(field);
        if (fieldIndex == 0)
        {
          result[0] = value;
          result[1] = eStorage;
        }
        else
        {
          result[0] = eStorage;
          result[1] = value;
        }
      }
      else
      {
        result = new Object [fieldCount + 1];
        Object[] oldStorage = (Object[])eStorage;
        for (int bit = CONTAINER, sourceIndex = 0, targetIndex = 0; bit <= RESOURCE; bit <<= 1)
        {
          if (bit == field)
          {
            result[targetIndex++] = value;
          }
          else if ((eFlags & bit) != 0)
          {
            result[targetIndex++] = oldStorage[sourceIndex++];
          }
        }
      }
      eStorage = result;
    }
    eFlags |= field;
  }

  private final void removeField(int field)
  {
    int fieldCount = Integer.bitCount(eFlags & FIELD_MASK);
    if (fieldCount == 1)
    {
      eStorage = null;
    }
    else
    {
      Object[] oldStorage = (Object[])eStorage;
      if (fieldCount == 2)
      {
        int fieldIndex = fieldIndex(field);
        eStorage = oldStorage[fieldIndex == 0 ? 1 : 0];
      }
      else
      {
        Object[] result = new Object [fieldCount - 1];
        for (int bit = CONTAINER, sourceIndex = 0, targetIndex = 0; bit <= RESOURCE; bit <<= 1)
        {
          if (bit == field)
          {
            sourceIndex++;
          }
          else if ((eFlags & bit) != 0)
          {
            result[targetIndex++] = oldStorage[sourceIndex++];
          }
        }
        eStorage = result;
      }
    }
    eFlags &= ~field;
  }

  @Override
  public EList<Adapter> eAdapters()
  {
    class ArrayDelegatingAdapterList extends ArrayDelegatingEList<Adapter> implements EObservableAdapterList, EScannableAdapterList
    {
      private static final long serialVersionUID = 1L;

      @Override
      protected Object[] newData(int capacity)
      {
        return new Adapter [capacity];
      }

      @Override
      public Object[] data()
      {
        return eBasicAdapterArray();
      }

      @Override
      public void setData(Object[] data)
      {
        ++modCount;
        if (data != null)
        {
          Adapter[] eContainerAdapterArray = eContainerAdapterArray();
          if (Arrays.equals(data, eContainerAdapterArray))
          {
            eBasicSetAdapterArray(eContainerAdapterArray);
            return;
          }
        }
        eBasicSetAdapterArray((Adapter[])data);
      }

      @Override
      protected void didAdd(int index, Adapter newAdapter)
      {
        newAdapter.setTarget(MinimalEObjectImpl.this);
        Listener[] listeners = eBasicAdapterListeners();
        if (listeners != null)
        {
          for (Listener listener : listeners)
          {
            listener.added(MinimalEObjectImpl.this, newAdapter);
          }
        }
      }

      @Override
      protected void didRemove(int index, Adapter oldAdapter)
      {
        Listener[] listeners = eBasicAdapterListeners();
        if (listeners != null)
        {
          for (Listener listener : listeners)
          {
            listener.removed(MinimalEObjectImpl.this, oldAdapter);
          }
        }
        Adapter adapter = oldAdapter;
        if (eDeliver())
        {
          Notification notification = new NotificationImpl(Notification.REMOVING_ADAPTER, oldAdapter, null, index)
            {
              @Override
              public Object getNotifier()
              {
                return MinimalEObjectImpl.this;
              }
            };
          adapter.notifyChanged(notification);
        }
        if (adapter instanceof Adapter.Internal)
        {
          ((Adapter.Internal)adapter).unsetTarget(MinimalEObjectImpl.this);
        }
        else if (adapter.getTarget() == MinimalEObjectImpl.this)
        {
          adapter.setTarget(null);
        }
      }

      public void addListener(Listener listener)
      {
        Listener[] listeners = eBasicAdapterListeners();
        if (listeners == null)
        {
          listeners = new Listener []{ listener };
        }
        else
        {
          Listener[] newListeners = new Listener [listeners.length + 1];
          System.arraycopy(listeners, 0, newListeners, 0, listeners.length);
          newListeners[listeners.length] = listener;
          listeners = newListeners;
        }
        eBasicSetAdapterListeners(listeners);
      }

      public void removeListener(Listener listener)
      {
        Listener[] listeners = eBasicAdapterListeners();
        if (listeners != null)
        {
          for (int i = 0; i < listeners.length; ++i)
          {
            if (listeners[i] == listener)
            {
              if (listeners.length == 1)
              {
                listeners = null;
              }
              else
              {
                Listener[] newListeners = new Listener [listeners.length - 1];
                System.arraycopy(listeners, 0, newListeners, 0, i);
                if (i != newListeners.length)
                {
                  System.arraycopy(listeners, i + 1, newListeners, i, newListeners.length - i);
                }
                listeners = newListeners;
              }
              eBasicSetAdapterListeners(listeners);
              break;
            }
          }
        }
      }

      public Adapter getAdapterForType(Object type)
      {
        Adapter[] adapters = eBasicAdapterArray();
        if (adapters != null)
        {
          for (Adapter adapter : adapters)
          {
            if (adapter.isAdapterForType(type))
            {
              return adapter;
            }
          }
        }
        return null;
      }
    }
    return new ArrayDelegatingAdapterList();
  }

  @Override
  protected Adapter[] eBasicAdapterArray()
  {
    return (Adapter[])getField(ADAPTER);
  }

  protected void eBasicSetAdapterArray(Adapter[] eAdapters)
  {
    setField(ADAPTER, eAdapters);
  }

  @Override
  protected boolean eBasicHasAdapters()
  {
    return hasField(ADAPTER);
  }

  protected EObservableAdapterList.Listener[] eBasicAdapterListeners()
  {
    return (EObservableAdapterList.Listener[])getField(ADAPTER_LISTENER);
  }

  protected void eBasicSetAdapterListeners(EObservableAdapterList.Listener[] eAdapterListeners)
  {
    setField(ADAPTER_LISTENER, eAdapterListeners);
  }

  @Override
  public boolean eDeliver()
  {
    return (eFlags & NO_DELIVER) == 0;
  }

  @Override
  public void eSetDeliver(boolean deliver)
  {
    if (deliver)
    {
      eFlags &= ~NO_DELIVER;
    }
    else
    {
      eFlags |= NO_DELIVER;
    }
  }

  @Override
  public boolean eIsProxy()
  {
    return hasField(PROXY);
  }

  @Override
  public URI eProxyURI()
  {
    return (URI)getField(PROXY);
  }

  @Override
  public void eSetProxyURI(URI uri)
  {
    setField(PROXY, uri);
  }

  @Override
  public InternalEObject eInternalContainer()
  {
    return (InternalEObject)getField(CONTAINER);
  }

  protected void eBasicSetContainer(InternalEObject newContainer)
  {
    setField(CONTAINER, newContainer);
  }

  @Override
  public int eContainerFeatureID()
  {
    return eFlags >> 16;
  }

  protected void eBasicSetContainerFeatureID(int newContainerFeatureID)
  {
    eFlags = newContainerFeatureID << 16 | (eFlags & 0x00FF);
  }

  @Override
  protected void eBasicSetContainer(InternalEObject newContainer, int newContainerFeatureID)
  {
    eBasicSetContainerFeatureID(newContainerFeatureID);
    eBasicSetContainer(newContainer);
  }

  @Override
  protected EClass eDynamicClass()
  {
    return (EClass)getField(CLASS);
  }

  @Override
  public EClass eClass()
  {
    EClass eClass = eDynamicClass();
    return eClass == null ? eStaticClass() : eClass;
  }

  @Override
  public void eSetClass(EClass eClass)
  {
    setField(CLASS, eClass);
  }

  @Override
  protected boolean eHasSettings()
  {
    return hasField(SETTING);
  }

  protected Object[] eBasicSettings()
  {
    return (Object[])getField(SETTING);
  }

  protected void eBasicSetSettings(Object[] settings)
  {
    setField(SETTING, settings);
  }

  @Override
  protected EStructuralFeature.Internal.DynamicValueHolder eSettings()
  {
    if (!eHasSettings())
    {
      int size = eClass().getFeatureCount() - eStaticFeatureCount();
      if (size != 0)
      {
        eBasicSetSettings(new Object [size]);
      }
    }

    return this;
  }

  @Override
  public Resource.Internal eDirectResource()
  {
    return (Resource.Internal)getField(RESOURCE);
  }

  @Override
  protected void eSetDirectResource(Resource.Internal resource)
  {
    setField(RESOURCE, resource);
  }

  @Override
  public EList<EObject> eContents()
  {
    return EContentsEList.createEContentsEList(this);
  }

  @Override
  public EList<EObject> eCrossReferences()
  {
    return ECrossReferenceEList.createECrossReferenceEList(this);
  }

  Object[] eDynamicSettings()
  {
    Object[] settings = eBasicSettings();
    if (settings == null)
    {
      eSettings();
      settings = eBasicSettings();
    }
    return settings;
  }

  public Object dynamicGet(int dynamicFeatureID)
  {
    Object[] settings = eDynamicSettings();
    return settings[dynamicFeatureID];
  }

  public void dynamicSet(int dynamicFeatureID, Object newValue)
  {
    Object[] settings = eDynamicSettings();
    settings[dynamicFeatureID] = newValue;
  }

  public void dynamicUnset(int dynamicFeatureID)
  {
    Object[] settings = eDynamicSettings();
    settings[dynamicFeatureID] = null;
  }
}
