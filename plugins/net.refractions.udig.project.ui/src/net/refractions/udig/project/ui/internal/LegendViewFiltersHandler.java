/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.project.ui.internal;

import net.refractions.udig.project.IMapCompositionListener;
import net.refractions.udig.project.MapCompositionEvent;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * The Filters Handler of the Legend View. This class is designed to handle the maintenance of the
 * Map Graphic Layers and Background Layers toggle functionality.
 * 
 * @author nchan
 * @since 1.3.1
 */
public class LegendViewFiltersHandler implements IMapCompositionListener {

    private Action toggleMgLayerAction;
    private Action toggleBgLayerAction;
    
    private MapGraphicLayerFilter mgLayerFilter;
    private BackgroundLayerFilter bgLayerFilter;
    
    private LegendView view;
    private Map map;
    
    /**
     * For test case use only
     * @return MapGraphicLayerFilter
     */
    public MapGraphicLayerFilter getMgLayerFilter() {
        return mgLayerFilter;
    }

    /**
     * For test case use only
     * @return BackgroundLayerFilter
     */
    public BackgroundLayerFilter getBgLayerFilter() {
        return bgLayerFilter;
    }

    public void setBgLayerFilter(boolean isOn) {
        bgLayerFilter.setShowLayer(isOn);
    }
    
    public boolean isBgInViewer(Viewer viewer, Object parent, Object element) {
        return bgLayerFilter.select(viewer, parent, element);
    }
    
    /**
     * Creates a LegendViewFiltersHandler
     * @param view
     */
    public LegendViewFiltersHandler(LegendView view) {
        this.view = view;
        this.mgLayerFilter = new MapGraphicLayerFilter();
        this.bgLayerFilter = new BackgroundLayerFilter();
    }
    
    /**
     * Sets the current map
     * @param map
     */
    public void setMap(Map map) {
        cleanHandler();
        initMap(map);
        setToggleLayersActionState();
    }
    
    /**
     * Cleans up the handler of listeners and objects.
     */
    public void disposeHandler() {
        
        cleanHandler();
        
        if (toggleBgLayerAction != null) {
            toggleBgLayerAction = null;    
        }
        if (toggleMgLayerAction != null) {
            toggleMgLayerAction = null;    
        }
        if (mgLayerFilter != null) {
            mgLayerFilter = null;
        }
        if (bgLayerFilter != null) {
            bgLayerFilter = null;
        }
        if (view != null) {
            view = null;    
        }
    }
    
    /**
     * Cleans the handler of listeners attached to the current map and the current map itself.
     */
    private void cleanHandler() {
        if (map != null) {
            map.removeMapCompositionListener(this);
            map = null;
        }
    }
    
    /**
     * Initialises the current map and adds listeners to it.
     * @param map
     */
    private void initMap(Map map) {
        this.map = map;
        if (map != null) {
            this.map.addMapCompositionListener(this);
        }
    }
    
    /**
     * Initialises and returns the toggleMgLayerAction action.  
     * @return toggleMgLayerAction
     */
    public Action getToggleMgAction() {
        
        if (toggleMgLayerAction == null) {
            toggleMgLayerAction = new FilterAction(null, IAction.AS_CHECK_BOX,
                    Messages.LegendView_show_mg_tooltip, Messages.LegendView_hide_mg_tooltip){
                @Override
                public void run() {
                    mgLayerFilter.setShowLayer(!this.isChecked());
                    super.run();
                }
            };
            toggleMgLayerAction.setToolTipText(Messages.LegendView_hide_mg_tooltip);
            toggleMgLayerAction.setImageDescriptor(ProjectUIPlugin.getDefault().getImageDescriptor(
                    ISharedImages.MAP_GRAPHIC_OBJ));
        }
        
        return toggleMgLayerAction;
    }

    /**
     * Initialises and returns the toggleBgLayerAction action.
     * @return toggleBgLayerAction
     */
    public Action getToggleBgAction() {
        
        if (toggleBgLayerAction == null) {
            toggleBgLayerAction = new FilterAction(null, IAction.AS_CHECK_BOX,
                    Messages.LegendView_show_bg_tooltip, Messages.LegendView_hide_bg_tooltip){
                @Override
                public void run() {
                    bgLayerFilter.setShowLayer(!this.isChecked());
                    super.run();
                }
            };
            toggleBgLayerAction.setToolTipText(Messages.LegendView_hide_bg_tooltip);
            toggleBgLayerAction.setImageDescriptor(ProjectUIPlugin.getDefault().getImageDescriptor(
                    ISharedImages.LAYER_OBJ));            
        }
                
        return toggleBgLayerAction;
    }
    
    /**
     * Handles the maintenance of the enabled/disabled state of the toggle actions.
     */
    public void setToggleLayersActionState() {
        
        if (map == null) {
            setLayerActionsEnabled(false);
        } else {
            if (map.getLayersInternal().size() > 0) {
                setLayerActionsEnabled(true);
            } else {
                setLayerActionsEnabled(false);
            }
        }
        
    }
    
    /**
     * Handles the enabling/disabling of the toggle actions.
     * @param enabled
     */
    private void setLayerActionsEnabled(boolean enabled) {
        if (toggleMgLayerAction != null) {
            toggleMgLayerAction.setEnabled(enabled);
        }
        if (toggleBgLayerAction != null) {
            toggleBgLayerAction.setEnabled(enabled);
        }
    }
    
    /**
     * Returns the filters for the map graphics and background layers toggling in the viewer.
     * @return
     */
    public ViewerFilter[] getFilters() {
        return new ViewerFilter[]{this.mgLayerFilter, this.bgLayerFilter};
    }
        
    /**
     * This abstract class is designed to be the base implementation of the toggle action buttons.
     * Provides mechanisms to toggle the tooltip of the button and call a method to refresh the
     * viewer.
     */
    private class FilterAction extends Action {
        
         private String showTooltip;
         private String hideTooltip;
        
         public FilterAction(String text, int style, String showTooltip, String hideTooltip) {
             super(text, style);
             this.showTooltip = showTooltip;
             this.hideTooltip = hideTooltip;
             setToolTipText(hideTooltip);
         }
         
         @Override
        public void run() {
             final boolean showLayer = this.isChecked();
             if (showLayer) { 
                 setToolTipText(showTooltip);
             } else {
                 setToolTipText(hideTooltip);
             }
             LegendView.getViewer().refresh();
             view.updateCheckboxes();
        }
         
    }
    
    
    /**
     * This abstract class provides the base implementation of the viewer filter used to manage
     * filtering the map graphics and background layers.
     */
    private abstract class AbstractLayerFilter extends ViewerFilter {

        private boolean showLayer = true;
        
        public void setShowLayer( boolean showLayer ) {
            this.showLayer = showLayer;
        }

        @Override
        public boolean select( Viewer viewer, Object parentElement, Object element ) {
            if (element instanceof Layer) {
                final Layer layer = (Layer) element;
                if (!this.showLayer && isLayerType(layer)) {
                    return false;

                }
            }
            return true;
        }

        protected abstract boolean isLayerType(Layer layer);
        
    }
    
    /**
     * This class provides the implementation of the map graphics filter.
     */
    private class MapGraphicLayerFilter extends AbstractLayerFilter {
        @Override
        protected boolean isLayerType( Layer layer ) {
            return LegendViewUtils.isMapGraphicLayer(layer);
        }
    }

    /**
     * This class provides the implementation of the background layers filter.
     */
    private class BackgroundLayerFilter extends AbstractLayerFilter {
        @Override
        protected boolean isLayerType( Layer layer ) {
            return LegendViewUtils.isBackgroundLayer(layer);
        }
    }

    /**
     * IMapCompositionListener method 
     */
    @Override
    public void changed( MapCompositionEvent event ) {
        if (MapCompositionEvent.EventType.ADDED == event.getType()) {
            final Layer layer = (Layer) event.getNewValue();
        } else if (MapCompositionEvent.EventType.REMOVED == event.getType()) {
            final Layer layer = (Layer) event.getOldValue();
        }
        setToggleLayersActionState();
    }
    
}
