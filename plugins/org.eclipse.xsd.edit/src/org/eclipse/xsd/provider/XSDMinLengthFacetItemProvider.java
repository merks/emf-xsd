/**
 * Copyright (c) 2002-2006 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors: 
 *   IBM - Initial API and implementation
 */
package org.eclipse.xsd.provider;


import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;

import org.eclipse.xsd.XSDMinLengthFacet;


/**
 * This is the item provider adpater for a {@link org.eclipse.xsd.XSDMinLengthFacet} object.
 */
public class XSDMinLengthFacetItemProvider
  extends XSDFixedFacetItemProvider
  implements 
    IEditingDomainItemProvider,
    IStructuredItemContentProvider, 
    ITreeItemContentProvider, 
    IItemLabelProvider, 
    IItemPropertySource
{
  /**
   * This constructs an instance from a factory and a notifier.
   */
  public XSDMinLengthFacetItemProvider(AdapterFactory adapterFactory)
  {
    super(adapterFactory);
  }

  /**
   * This returns the property descriptors for the adapted class.
   */
  @Override
  public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object)
  {
    if (itemPropertyDescriptors == null)
    {
      super.getPropertyDescriptors(object);

      // This is for the value feature.
      //
      itemPropertyDescriptors.add
        (new ItemPropertyDescriptor
          (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
           XSDEditPlugin.INSTANCE.getString("_UI_Value_label"),
           XSDEditPlugin.INSTANCE.getString("_UI_ValueMinLengthFacet_description"),
           xsdPackage.getXSDMinLengthFacet_Value(),
           true,
           ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE));

    }
    return itemPropertyDescriptors;
  }

  /**
   * This returns XSDMinLengthFacet.gif.
   */
  @Override
  public Object getImage(Object object)
  {
    return XSDEditPlugin.INSTANCE.getImage("full/obj16/XSDMinLengthFacet");
  }

  @Override
  public String getText(Object object)
  {
    XSDMinLengthFacet xsdMinLengthFacet = ((XSDMinLengthFacet)object);
    String result = xsdMinLengthFacet.getLexicalValue();
    return result == null ? "" : result;
  }

  /**
   * This handles notification by calling {@link #fireNotifyChanged fireNotifyChanged}.
   */
  @Override
  public void notifyChanged(Notification msg) 
  {
    if (
         msg.getFeature() == xsdPackage.getXSDFacet_LexicalValue()
       )
    {
      fireNotifyChanged(msg);
      return;
    }
    super.notifyChanged(msg);
  }

}
