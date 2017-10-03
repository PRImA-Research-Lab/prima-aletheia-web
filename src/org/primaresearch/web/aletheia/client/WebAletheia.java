/*
 * Copyright 2015 PRImA Research Lab, University of Salford, United Kingdom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primaresearch.web.aletheia.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.primaresearch.dla.page.layout.physical.shared.RegionType;
import org.primaresearch.maths.geometry.Dimension;
import org.primaresearch.shared.variable.DoubleValue;
import org.primaresearch.shared.variable.Variable;
import org.primaresearch.shared.variable.VariableMap;
import org.primaresearch.web.gwt.client.log.LogManager;
import org.primaresearch.web.gwt.client.page.PageLayoutC;
import org.primaresearch.web.gwt.client.page.PageSyncManager;
import org.primaresearch.web.gwt.client.page.PageSyncManager.PageSyncListener;
import org.primaresearch.web.gwt.client.ui.DocumentImageListener;
import org.primaresearch.web.gwt.client.ui.DocumentImageLoader;
import org.primaresearch.web.gwt.client.ui.page.AttributeEditor;
import org.primaresearch.web.gwt.client.ui.page.PageScrollView;
import org.primaresearch.web.gwt.client.ui.page.PageScrollView.ZoomChangeListener;
import org.primaresearch.web.gwt.client.ui.page.ReadingOrderTreeView;
import org.primaresearch.web.gwt.client.ui.page.RegionPropertiesView;
import org.primaresearch.web.gwt.client.ui.page.RegionPropertiesView.PropertiesViewClickListener;
import org.primaresearch.web.gwt.client.ui.page.RegionTypeEditor;
import org.primaresearch.web.gwt.client.ui.page.RegionTypeEditor.RegionTypeSelectionListener;
import org.primaresearch.web.gwt.client.ui.page.SelectionManager;
import org.primaresearch.web.gwt.client.ui.page.SelectionManager.SelectionListener;
import org.primaresearch.web.gwt.client.ui.page.TextContentView;
import org.primaresearch.web.gwt.client.ui.page.TextContentView.TextContentViewChangeListener;
import org.primaresearch.web.gwt.client.ui.page.renderer.ContentSelectionRendererPlugin;
import org.primaresearch.web.gwt.client.ui.page.renderer.PageContentRendererPlugin;
import org.primaresearch.web.gwt.client.ui.page.renderer.ReadingOrderRendererPlugin;
import org.primaresearch.web.gwt.client.ui.page.renderer.SelectionFocusRendererPlugin;
import org.primaresearch.web.gwt.client.ui.page.tool.CreatePageObjectTool;
import org.primaresearch.web.gwt.client.ui.page.tool.CreatePageObjectTool.DrawingTool;
import org.primaresearch.web.gwt.client.ui.page.tool.DeleteContentObjectTool;
import org.primaresearch.web.gwt.client.ui.page.tool.MoveRegionTool;
import org.primaresearch.web.gwt.client.ui.page.tool.ResizeRegionTool;
import org.primaresearch.web.gwt.client.ui.page.tool.controls.ContentObjectToolbar;
import org.primaresearch.web.gwt.client.ui.page.tool.controls.ContentObjectToolbarButton;
import org.primaresearch.web.gwt.client.ui.page.tool.drawing.EditOutlineTool;
import org.primaresearch.web.gwt.client.ui.page.tool.drawing.PageViewTool;
import org.primaresearch.web.gwt.client.ui.page.tool.drawing.PageViewToolListener;
import org.primaresearch.web.gwt.client.user.UserManager;
import org.primaresearch.web.gwt.client.user.UserManager.LogOnListener;
import org.primaresearch.web.gwt.shared.page.ContentObjectC;
import org.primaresearch.web.gwt.shared.page.ContentObjectSync;
import org.primaresearch.web.gwt.shared.user.DefaultPermissionNames;
import org.primaresearch.web.gwt.shared.user.Permissions;
import org.primaresearch.web.gwt.shared.variable.GwtDecimalFormatter;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point class for WebAletheia.<br>
 * <br>
 * Entry point classes define <code>onModuleLoad()</code>.
 * 
 * @author Christian Clausner
 */
public class WebAletheia implements EntryPoint, ResizeHandler, DocumentImageListener, ZoomChangeListener,
									KeyPressHandler, KeyDownHandler, LogOnListener, PageSyncListener, SelectionListener,
									RegionTypeSelectionListener, TextContentViewChangeListener, PropertiesViewClickListener {

	private static final int RESIZE_REQUIRED_EXTRA_HEIGHT = 65; //Height for toolbar and margin (needed for resize handling)
	
	private static final int ERROR_ON_MODULE_LOAD 					= 1;
	private static final int ERROR_ENABLING_CONTROLS 				= 2;
	private static final int ERROR_CREATING_REGION_TYPE_DIALOG		= 3;
	private static final int ERROR_LOADING_DOCUMENT_DATA 			= 4;
	private static final int ERROR_ON_RESIZE						= 5;
	private static final int ERROR_UNSPECIFIED						= 6;
	private static final int ERROR_SHOWING_HELP_DIALOG				= 8;
	private static final int ERROR_REGION_TYPE_SELECTION_CHANGE 	= 10;
	private static final int ERROR_ON_SELECTION_CHANGE				= 12;

	private LogManager logManager = new LogManager("WebAletheia");
	private UserManager userManager = new UserManager();
	private SelectionManager selectionManager = new SelectionManager();
	private PageScrollView pageView;
	private PageLayoutC pageLayout;
	private RegionPropertiesView propertiesView;
	private DialogBox propertyEditorDialog;
	private AttributeEditor attributeEditor = null;
	private TextContentView textContentView;
	private DocumentImageLoader imageLoader;
	private PageSyncManager pageSync;
	private String currentPageObjectType = "Region";
	private SplitLayoutPanel splitPanel;
	private TabPanel rightTabPanel;
	private ScrollPanel readingOrderPanel;
	private SplitLayoutPanel rightSplitPanel;
	private PageContentRendererPlugin pageContentRendererPlugin;
	private ContentSelectionRendererPlugin selectionRendererPlugin;
	private ReadingOrderRendererPlugin readingOrderRendererPlugin;
	private HorizontalPanel toolbar;
	private PushButton zoomIn;
	private PushButton zoomOut;
	private PushButton createRectRegion;
	private PushButton changeRegionType;
	private PushButton downloadPage;
	private DialogBox regionTypeDialog;
	private RegionType defaultRegionType = null;
	private FocusPanel focusPanel;
	private VerticalPanel verticalPanel;
	private ToggleButton showRegions;
	private ToggleButton showTextLines;
	private ToggleButton showWords;
	private ToggleButton showGlyphs;
	private ToggleButton showBorder;
	private ToggleButton showPrintSpace;
	private Image loaderImage;
	private int loadedDataCount = 0;
	private DialogBox errorMessageBox;
	private Label errorMessage;
	private Timer errorMessageTimer;
	private boolean textChanged = false;
	private boolean imageLoaded = false;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		try {
			//Set the number formatter (had to be decoupled since GWT doesn't work with DecimalFormat)
			DoubleValue.setFormatter(new GwtDecimalFormatter(DoubleValue.defaultFormatPattern));
			
			//Initialisations
			imageLoader = new DocumentImageLoader();
			pageLayout = new PageLayoutC();
			pageSync = new PageSyncManager(null, pageLayout);
			
			pageView = new PageScrollView(pageLayout, imageLoader, selectionManager, true, false);
			
			pageView.getRenderer().addPlugin(pageContentRendererPlugin = new PageContentRendererPlugin());
			pageView.getRenderer().addPlugin(selectionRendererPlugin = new ContentSelectionRendererPlugin());
			pageView.getRenderer().addPlugin(readingOrderRendererPlugin = new ReadingOrderRendererPlugin());
			
			pageView.addZoomListener(this);
			pageSync.addListener(pageView);
			pageSync.addListener(this);
			imageLoader.addListener(pageView);
			imageLoader.addListener(this);
			selectionManager.addListener(this);
			
			verticalPanel = new VerticalPanel();
			verticalPanel.setSize("99.8%", (Window.getClientHeight()-1)+"px");
			focusPanel = new FocusPanel(verticalPanel);
			focusPanel.setSize("99.8%", (Window.getClientHeight()-1)+"px");
			focusPanel.addKeyPressHandler(this);
			focusPanel.addKeyDownHandler(this);
			focusPanel.getElement().getStyle().setOutlineWidth(0, Unit.PX);
			RootPanel.get().add(focusPanel);
			focusPanel.setFocus(true);
			
			//Toolbar panel at the top
			toolbar = new HorizontalPanel();
			verticalPanel.add(toolbar);
			toolbar.addStyleName("paddedHorizontalPanel"); 
			toolbar.setWidth("100%");
			
			//Zoom in button
			Image img = new Image("img/zoom_in.png");
			img.getElement().getStyle().setDisplay(Display.BLOCK);
			img.getElement().getStyle().setProperty("margin", "auto");
			img.getElement().getStyle().setMarginTop(3, Unit.PX);
			zoomIn = new PushButton(img);
			toolbar.add(zoomIn);
			zoomIn.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					pageView.zoomIn();
				}
			});
			
			//Zoom out button
			img = new Image("img/zoom_out.png");
			img.getElement().getStyle().setDisplay(Display.BLOCK);
			img.getElement().getStyle().setProperty("margin", "auto");
			img.getElement().getStyle().setMarginTop(3, Unit.PX);
			zoomOut = new PushButton(img);
			toolbar.add(zoomOut);
			zoomOut.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					pageView.zoomOut();
				}
			});
	
			//Zoom to fit button
			img = new Image("img/zoom_to_fit.png");
			img.getElement().getStyle().setDisplay(Display.BLOCK);
			img.getElement().getStyle().setProperty("margin", "auto");
			img.getElement().getStyle().setMarginTop(3, Unit.PX);
			PushButton zoomFit = new PushButton(img);
			toolbar.add(zoomFit);
			zoomFit.setTitle("Zoom to fit document");
			zoomFit.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					pageView.zoomToFitPage();
				}
			});
			
			//Zoom to width button
			img = new Image("img/zoom_to_width.png");
			img.getElement().getStyle().setDisplay(Display.BLOCK);
			img.getElement().getStyle().setProperty("margin", "auto");
			img.getElement().getStyle().setMarginTop(3, Unit.PX);
			PushButton zoomWidth = new PushButton(img);
			toolbar.add(zoomWidth);
			zoomWidth.setTitle("Zoom to document width");
			zoomWidth.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					pageView.zoomToFit(pageLayout.getWidth(), 1, false);
					pageView.centerRectangle(0, 0, pageLayout.getWidth()-1, pageLayout.getHeight()-1, false);
				}
			});
			
			Label label = new Label("Content:");
			toolbar.add(label);

			//Content selection
			HorizontalPanel contentTypePanel = new HorizontalPanel();
			contentTypePanel.addStyleName("contentTypePanel"); 
			toolbar.add(contentTypePanel);
			showRegions = new ToggleButton("Regions"); 
			contentTypePanel.add(showRegions);    
			showRegions.setDown(true);
			showTextLines = new ToggleButton("Text Lines"); 
			contentTypePanel.add(showTextLines);    
			showWords = new ToggleButton("Words"); 
			contentTypePanel.add(showWords);    
			showGlyphs = new ToggleButton("Glyphs"); 
			contentTypePanel.add(showGlyphs);    
			showBorder = new ToggleButton("Border"); 
			contentTypePanel.add(showBorder);
			showPrintSpace = new ToggleButton("Print Space"); 
			contentTypePanel.add(showPrintSpace);    
			
			showRegions.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					loadPageContent(PageLayoutC.TYPE_Regions);
					updateContentSelectionControl(PageLayoutC.TYPE_Regions);
				}
			});
			showTextLines.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (showTextLines.isDown()) 
						loadPageContent(PageLayoutC.TYPE_TextLines); 
					updateContentSelectionControl(PageLayoutC.TYPE_TextLines);
				}
			});
			showWords.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (showWords.isDown())
						loadPageContent(PageLayoutC.TYPE_Words); 
					updateContentSelectionControl(PageLayoutC.TYPE_Words);
				}
			});
			showGlyphs.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (showGlyphs.isDown()) 
						loadPageContent(PageLayoutC.TYPE_Glyphs); 
					updateContentSelectionControl(PageLayoutC.TYPE_Glyphs);
				}
			});
			showBorder.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (showBorder.isDown()) 
						loadPageContent(PageLayoutC.TYPE_Border); 
					updateContentSelectionControl(PageLayoutC.TYPE_Border);
				}
			});
			showPrintSpace.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (showPrintSpace.isDown()) 
						loadPageContent(PageLayoutC.TYPE_Printspace); 
					updateContentSelectionControl(PageLayoutC.TYPE_Printspace);
				}
			});
			
			//Create region tool
			img = new Image("img/new_object.png");
			img.getElement().getStyle().setDisplay(Display.BLOCK);
			img.getElement().getStyle().setProperty("margin", "auto");
			img.getElement().getStyle().setMarginTop(3, Unit.PX);
			createRectRegion = new PushButton(img);
			createRectRegion.setTitle("Create new object");
			toolbar.add(createRectRegion);
			createRectRegion.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {

					if (PageLayoutC.TYPE_Regions.equals(currentPageObjectType)) {
						if (defaultRegionType != null)
							new CreatePageObjectTool(DrawingTool.Rectangle, pageView, defaultRegionType, pageSync);
						else
							new CreatePageObjectTool(DrawingTool.Rectangle, pageView, currentPageObjectType, pageSync);
					} else
						new CreatePageObjectTool(DrawingTool.Rectangle, pageView, currentPageObjectType, pageSync);
				}
			});
			createRectRegion.setVisible(false);
				
			//Region type
			img = new Image("img/region_type.png");
			img.getElement().getStyle().setDisplay(Display.BLOCK);
			img.getElement().getStyle().setProperty("margin", "auto");
			img.getElement().getStyle().setMarginTop(3, Unit.PX);
			changeRegionType = new PushButton(img);
			changeRegionType.setTitle("Change type of selected region");
			toolbar.add(changeRegionType);
			changeRegionType.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
			        regionTypeDialog.center();
			        regionTypeDialog.show();			
			    }
			});
			changeRegionType.setVisible(false);
			changeRegionType.setEnabled(false);
	
			//Prepare dialog
			createRegionTypeDialog();
	
			//Separator
			label = new Label(" ");
			toolbar.add(label);
	
			img = new Image("img/download_page.png");
			img.getElement().getStyle().setDisplay(Display.BLOCK);
			img.getElement().getStyle().setProperty("margin", "auto");
			img.getElement().getStyle().setMarginTop(3, Unit.PX);
			downloadPage = new PushButton(img);
			downloadPage.setTitle("Download page content");
			toolbar.add(downloadPage);
			downloadPage.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					String url = GWT.getModuleBaseURL() + "documentPageSync?downloadPage="+pageSync.getUrl();
					Window.open( url, "_blank", "status=0,toolbar=0,menubar=0,location=0");
				}
			});
			
			//Separator
			label = new Label(" ");
			toolbar.add(label);
	
			//Help button
			img = new Image("img/help.png"); 
			img.setTitle("Show help"); 
			img.getElement().getStyle().setDisplay(Display.BLOCK);
			img.getElement().getStyle().setProperty("margin", "auto");  
			PushButton helpButton = new PushButton(img);
			toolbar.add(helpButton);
			helpButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					showHelp();
				}
			});
			
			//Jump to document panel
			HorizontalPanel jumpTodocPanel = new HorizontalPanel();
			toolbar.add(jumpTodocPanel);
			jumpTodocPanel.addStyleName("jumpToDocPanel");
			toolbar.setCellWidth(jumpTodocPanel, "100%");
			
			//Load animation
			loaderImage = new Image("img/loaderb32.gif"); 
			loaderImage.setTitle("Loading"); 
			loaderImage.getElement().getStyle().setDisplay(Display.BLOCK);
			loaderImage.getElement().getStyle().setProperty("margin", "auto");  
			jumpTodocPanel.add(loaderImage);
	
			//Split panel with page view and region properties / reading order
			splitPanel = new SplitLayoutPanel();
			verticalPanel.add(splitPanel);
			splitPanel.setSize("99.8%", (Window.getClientHeight()-RESIZE_REQUIRED_EXTRA_HEIGHT)+"px");
			splitPanel.addStyleName("mainPanel");
			
			//Tab panel for properties and reading order
			rightTabPanel = new TabPanel();
			rightTabPanel.getDeckPanel().setHeight("100%"); 
			splitPanel.addEast(rightTabPanel, 280);
			rightTabPanel.setSize("100%", (Window.getClientHeight()-RESIZE_REQUIRED_EXTRA_HEIGHT)+"px");
			rightTabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
				
				@Override
				public void onSelection(SelectionEvent<Integer> event) {
					int index = event.getSelectedItem();
					//Properties tab
					if (index == 0) {
						readingOrderRendererPlugin.enable(false);
						pageView.getRenderer().refresh();
					}
					//Reading order tab
					else if (index == 1) {
						readingOrderRendererPlugin.enable(true);
						if (pageLayout.getReadingOrder() == null)
							pageSync.loadReadingOrderAsync();
						else
							pageView.getRenderer().refresh();
					}
				}
			});
			
			//First tab: Another split panel for region properties and text content
			rightSplitPanel = new SplitLayoutPanel();
			rightTabPanel.add(rightSplitPanel, "Properties");
			rightSplitPanel.setWidth("100%");
			rightSplitPanel.setHeight("100%");
			
			//Text content
			textContentView = new TextContentView();
			rightSplitPanel.addSouth(textContentView.getWidget(), 280);
			selectionManager.addListener(textContentView);
			textContentView.getWidget().addStyleName("textContentView");
			textContentView.addChangeListener(this);
	
			//Region properties
			propertiesView = new RegionPropertiesView();
			selectionManager.addListener(propertiesView);
			propertiesView.getWidget().addStyleName("regionPropertiesView");
			rightSplitPanel.add(propertiesView.getWidget()); //Centre
			rightSplitPanel.getWidgetContainerElement(propertiesView.getWidget()).getStyle().setOverflowY(Overflow.AUTO);

			//Second tab: Reading order
			readingOrderPanel = new ScrollPanel();
			readingOrderPanel.addStyleName("readingOrderPanel");
			rightTabPanel.add(readingOrderPanel, "Reading Order");
			ReadingOrderTreeView readingOrderView = new ReadingOrderTreeView(pageLayout, selectionManager);
			pageSync.addListener(readingOrderView);
			readingOrderPanel.add(readingOrderView.getWidget());
			//readingOrderView.getWidget().getElement().getStyle().setOverflow(Overflow.AUTO);
			//readingOrderView.getWidget().getElement().getStyle().setOverflowY(Overflow.AUTO);
			
			rightTabPanel.selectTab(0);

			//Page view
			splitPanel.add(pageView); //Centre
			pageView.asWidget().addStyleName("pageView");
	
			//Resize handler (browser window resize events)
			Window.addResizeHandler(this);
	
			//Authenticate user
			userManager.addListener(this);
			userManager.logOn(	Window.Location.getParameter("Appid"), 
								Window.Location.getParameter("Did"),
								Window.Location.getParameter("Aid"),
								Window.Location.getParameter("a"));
		} catch (Exception exc) {
			logManager.logError(ERROR_ON_MODULE_LOAD, "Error in onModuleLoad()");
			exc.printStackTrace();
		}
	}
	
	private void updateContentSelectionControl(String currentType) {
		showRegions.setDown(PageLayoutC.TYPE_Regions.equals(currentType));
		showTextLines.setDown(PageLayoutC.TYPE_TextLines.equals(currentType));
		showWords.setDown(PageLayoutC.TYPE_Words.equals(currentType));
		showGlyphs.setDown(PageLayoutC.TYPE_Glyphs.equals(currentType));
		showBorder.setDown(PageLayoutC.TYPE_Border.equals(currentType));
		showPrintSpace.setDown(PageLayoutC.TYPE_Printspace.equals(currentType));
	}
	
	@Override
	public void logOnSuccessful(UserManager userManager) {
		try {
			//Load image and content
			loadedDataCount = 0;
			imageLoader.loadImage(userManager.getDocumentImageWebServiceUrl());
			pageSync.getPageSizeAsync();
			pageSync.loadContentObjectsAsync(PageLayoutC.TYPE_Regions);
			enableControls();
		} catch (Exception exc) {
			logManager.logError(ERROR_LOADING_DOCUMENT_DATA, "Error on triggering loading the document data");
			exc.printStackTrace();
		}
	}

	@Override
	public void logOnFailed(UserManager userManager) {
		//loaderImage.setVisible(false);
		
		//Demo mode
		imageLoader.loadImage("img/demo.jpg");
		pageSync.setUrl("demo");
		pageSync.getPageSizeAsync();
		pageSync.loadContentObjectsAsync(PageLayoutC.TYPE_Regions);
		//readOnly = false;
		Permissions permissions = userManager.getPermissions();
		if (permissions == null) {
			permissions = new Permissions();
			userManager.setPermissions(permissions);
		}
		
		DefaultPermissionNames.giveDemoPermissions(permissions);
		
		enableControls();
	}
	
	/**
	 * Enables or disables user interface controls according to the user's permissions
	 */
	private void enableControls() {
		try {
			boolean canEdit = false;
			boolean canDownload = false;
			Permissions permissions = userManager.getPermissions();
			if (permissions != null) {
				canEdit = permissions.isPermitted(DefaultPermissionNames.Edit);
				canDownload = permissions.isPermitted(DefaultPermissionNames.Download);
			}
			createRectRegion.setVisible(canEdit);
			changeRegionType.setVisible(canEdit);
			downloadPage.setVisible(canDownload);
			textContentView.setReadOnly(!canEdit);
			
			if (canEdit)
				propertiesView.addListener(this);
			else
				propertiesView.removeListener(this);
			propertiesView.setPositionOfEmptyAttributes(!canEdit);
			
			addToolIconsToPageView();
		} catch (Exception exc) {
			logManager.logError(ERROR_ENABLING_CONTROLS, "Error in enableControls()");
			exc.printStackTrace();
		}
	}
	
	/**
	 * Loads the page content objects of the given type (contentType drop-down box) from the server.
	 */
	private void loadPageContent(String type) {
		try {
			selectionManager.clearSelection();
			
			loaderImage.setVisible(true);
			loadedDataCount--;
	
			currentPageObjectType = type;
			pageSync.loadContentObjectsAsync(type);
		} catch (Exception exc) {		
			logManager.logError(ERROR_LOADING_DOCUMENT_DATA, "Error on triggering loading the document page content");
			exc.printStackTrace();
		}
	}
	
	@Override
	public void contentLoaded(String contentType) {
		try {
			loadedDataCount++;
			if (loadedDataCount >= 2)
				loaderImage.setVisible(false);

			//Mark all content objects as 'read-only'
			boolean readOnly = true;
			Permissions permissions = userManager.getPermissions();
			if (permissions != null)
				readOnly = !permissions.isPermitted(DefaultPermissionNames.Edit);

			if (readOnly) {
				List<ContentObjectC> contentObjects = pageLayout.getContent(contentType);
				if (contentObjects != null) {
					for (Iterator<ContentObjectC> it = contentObjects.iterator(); it.hasNext(); ) {
						it.next().setReadOnly(true);
					}
				}
			}
			
			//Hide region type dialogue for text lines, words, etc.
			if (!PageLayoutC.TYPE_Regions.equals(contentType))
				regionTypeDialog.hide();
			
			textChanged = false;
		} catch (Exception exc) {
			logManager.logError(ERROR_UNSPECIFIED, "Error post-processing loaded page content");
			exc.printStackTrace();
		}
	}


	@Override
	public void onResize(ResizeEvent event) {
		try {
			verticalPanel.setSize("99.8%", (Window.getClientHeight()-1)+"px");
			focusPanel.setSize("99.8%", (Window.getClientHeight()-1)+"px");
			splitPanel.setSize("99.8%", (Window.getClientHeight()-RESIZE_REQUIRED_EXTRA_HEIGHT)+"px");
			rightTabPanel.setSize("99.8%", (Window.getClientHeight()-RESIZE_REQUIRED_EXTRA_HEIGHT)+"px");
			readingOrderPanel.setSize("100%", (Window.getClientHeight()-RESIZE_REQUIRED_EXTRA_HEIGHT-50)+"px");
		} catch (Exception exc) {
			logManager.logError(ERROR_ON_RESIZE, "Error in onResize()");
			exc.printStackTrace();
		}
	}

	@Override
	public void imageLoaded() {
		try {
			imageLoaded = true;
			loadedDataCount++;
			if (loadedDataCount >= 2)
				loaderImage.setVisible(false);
			
			pageLayout.setWidth(imageLoader.getOriginalImageWidth());
			pageLayout.setHeight(imageLoader.getOriginalImageHeight());
			
			pageView.zoomToFitPage();
		} catch (Exception exc) {
			logManager.logError(ERROR_UNSPECIFIED, "Error after document page image has been loaded");
			exc.printStackTrace();
		}
	}
	
	/**
	 * Adds tool controls to the page view.
	 */
	void addToolIconsToPageView() {
		boolean canEdit = false;
		Permissions permissions = userManager.getPermissions();
		if (permissions != null)
			canEdit = permissions.isPermitted(DefaultPermissionNames.Edit);

		//Move region tool
		if (canEdit)
			pageView.addHoverWidget(new MoveRegionTool("img/move.png", selectionManager, pageSync, true, false));

		//Resize tools
		if (canEdit) {
			pageView.addHoverWidget(new ResizeRegionTool(ResizeRegionTool.TYPE_LEFT, "img/resize.png", selectionManager, pageSync, true));
			pageView.addHoverWidget(new ResizeRegionTool(ResizeRegionTool.TYPE_RIGHT, "img/resize.png", selectionManager, pageSync, true));
			pageView.addHoverWidget(new ResizeRegionTool(ResizeRegionTool.TYPE_TOP, "img/resize.png", selectionManager, pageSync, true));
			pageView.addHoverWidget(new ResizeRegionTool(ResizeRegionTool.TYPE_BOTTOM, "img/resize.png", selectionManager, pageSync, true));
			pageView.addHoverWidget(new ResizeRegionTool(ResizeRegionTool.TYPE_TOP_LEFT, "img/resize.png", selectionManager, pageSync, true));
			pageView.addHoverWidget(new ResizeRegionTool(ResizeRegionTool.TYPE_TOP_RIGHT, "img/resize.png", selectionManager, pageSync, true));
			pageView.addHoverWidget(new ResizeRegionTool(ResizeRegionTool.TYPE_BOTTOM_LEFT, "img/resize.png", selectionManager, pageSync, true));
			pageView.addHoverWidget(new ResizeRegionTool(ResizeRegionTool.TYPE_BOTTOM_RIGHT, "img/resize.png", selectionManager, pageSync, true));
		}
		
		//Toolbar for selected objects
		if (canEdit) {
			ContentObjectToolbar objectToolbar = new ContentObjectToolbar(selectionManager);
			pageView.addHoverWidget(objectToolbar);
		
			//Edit object outline tool
			ContentObjectToolbarButton editOutline = new ContentObjectToolbarButton("img/editOutline.png", "Edit outline");
			objectToolbar.add(editOutline);
			editOutline.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					ContentObjectC selObj = null;
					if (selectionManager.getSelection().size() == 1) {
						selObj = selectionManager.getSelection().iterator().next();
						pageContentRendererPlugin.enableHighlight(false);
						pageContentRendererPlugin.setGreyedOut(true);
						selectionRendererPlugin.enable(false);
						final SelectionFocusRendererPlugin focusRendererPlugin = new SelectionFocusRendererPlugin();
						pageView.getRenderer().addPlugin(focusRendererPlugin);
						EditOutlineTool tool = new EditOutlineTool(selObj, pageView, selectionManager, pageSync); 
						pageView.setTool(tool);
						tool.addListener(new PageViewToolListener() {
							@Override
							public void onToolFinished(PageViewTool tool, boolean success) {
								pageContentRendererPlugin.enableHighlight(true);
								pageContentRendererPlugin.setGreyedOut(false);
								selectionRendererPlugin.enable(true);
								pageView.getRenderer().removePlugin(focusRendererPlugin);
								pageView.getRenderer().refresh();
							}
						});
					}
				}
			});
			
			//Delete object tool
			final ContentObjectToolbarButton deleteObject = new ContentObjectToolbarButton("img/delete.png", "Delete object"); 
			objectToolbar.add(deleteObject);
			deleteObject.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					ContentObjectC selObj = null;
					if (selectionManager.getSelection().size() == 1) {
						selObj = selectionManager.getSelection().iterator().next();
					}
					DeleteContentObjectTool.run(pageView.getViewPanel(), deleteObject.asWidget(), pageLayout, selObj, pageSync, selectionManager);
				}
			});
				
			objectToolbar.refresh();
		}
	}

	@Override
	public void zoomChanged(double newZoomFactor, double oldZoomFactor, boolean isMinZoom, boolean isMaxZoom) {
		if (zoomIn != null)
			zoomIn.setEnabled(!isMaxZoom);
		if (zoomOut != null)
			zoomOut.setEnabled(!isMinZoom);
	}
	
	/**
	 * Displays a dialogue with help (see help.html)
	 */
	private void showHelp() {
		try {
			final DialogBox helpDialog = new DialogBox(true);
	
			Frame frame = new Frame("Help.html");
			frame.setHeight("400px");
			frame.setWidth("350px");
			frame.getElement().getStyle().setBorderWidth(0, Unit.PX);
			helpDialog.setWidget(frame);
			//HTML html = new HT
			
			helpDialog.center();
			
			helpDialog.show();
		} catch (Exception exc) {
			logManager.logError(ERROR_SHOWING_HELP_DIALOG, "Error on trying to display the help dialogue");
			exc.printStackTrace();
		}
	}

	@Override
	public void onKeyPress(KeyPressEvent event) {
		int code = event.getUnicodeCharCode(); 
		if (code == '\u002B')
			pageView.zoomIn();
		else if (code == '\u2212' || code == '\u2010' || code == '\u002D')
			pageView.zoomOut();
		//else
		//	Window.alert(""+code);
		
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		if (event.isRightArrow())
			pageView.getScrollPanel().scroll(10, 0);
		else if (event.isLeftArrow())
			pageView.getScrollPanel().scroll(-10, 0);
		else if (event.isUpArrow())
			pageView.getScrollPanel().scroll(0, -10);
		else if (event.isDownArrow())
			pageView.getScrollPanel().scroll(0, 10);
	}

	@Override
	public void contentLoadingFailed(String contentType, Throwable caught) {
		showErrorDialogue("Page content could not be loaded.");		
	}

	@Override
	public void pageIdLoaded(String id) {
	}

	@Override
	public void pageIdLoadingFailed(Throwable caught) {
		showErrorDialogue("Page ID could not be loaded.");		
	}

	@Override
	public void contentObjectAdded(ContentObjectSync syncObj,
			ContentObjectC localObj) {
	}

	@Override
	public void contentObjectAddingFailed(ContentObjectC object,
			Throwable caught) {
		showErrorDialogue("Page content object could not be added.");		
	}

	@Override
	public void contentObjectDeleted(ContentObjectC object) {
	}

	@Override
	public void contentObjectDeletionFailed(ContentObjectC object,
			Throwable caught) {
		showErrorDialogue("Deletion failed.");		
	}

	@Override
	public void textContentSynchronized(ContentObjectC object) {
	}

	@Override
	public void textContentSyncFailed(ContentObjectC object, Throwable caught) {
		showErrorDialogue("Text not synchronised.");		
	}

	@Override
	public void regionTypeSynchronized(ContentObjectC remoteObject, ArrayList<String> childObjectsToDelete) {
		try {
			//Find the local object
			ContentObjectC localObject = pageLayout.findContentObject(remoteObject.getId());
			
			//Copy all properties
			localObject.setType(remoteObject.getType());
			localObject.setAttributes(remoteObject.getAttributes());
			
			System.out.println("Type changed: "+remoteObject.getType().getName()); 
			String subtype = "none"; 
			VariableMap attrs = localObject.getAttributes();
			if (attrs != null) {
				Variable attr = attrs.get("type"); 
				if (attr != null)
					subtype = ""+attr.getValue(); 
			}
			System.out.println(" Subtype: "+subtype); 
			
			//Delete obsolete child objects if necessary
			if (childObjectsToDelete != null) {
				for (int i=0; i<childObjectsToDelete.size(); i++) {
					pageLayout.remove(pageLayout.findContentObject(childObjectsToDelete.get(i)));
				}
			}
			
			pageView.getRenderer().refresh();
			propertiesView.selectionChanged(selectionManager);
						
			if (localObject.getType() != RegionType.TextRegion)
				textContentView.clear();
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	@Override
	public void regionTypeSyncFailed(ContentObjectC object, Throwable caught) {
		showErrorDialogue("Region type not synchronised.");
	}

	@Override
	public void objectOutlineSynchronized(ContentObjectC object) {
	}

	@Override
	public void objectOutlineSyncFailed(ContentObjectC object, Throwable caught) {
		showErrorDialogue("Object outline not synchronised.");
	}

	@Override
	public void pageFileSaved() {
	}

	@Override
	public void pageFileSaveFailed(Throwable caught) {
		showErrorDialogue("Save failed.");
	}

	@Override
	public void changesReverted() {
		textChanged = false;
	}

	@Override
	public void revertChangesFailed(Throwable caught) {
		showErrorDialogue("Revert failed.");
	}

	@Override
	public void selectionChanged(SelectionManager manager) {
		try {
			ContentObjectC selectedContentObject = null;
			
			//Update region type editor heading
			if (manager.getSelection().size() == 0) //Nothing selected
				changeRegionType.setEnabled(false);
			else {
				selectedContentObject = selectionManager.getSelection().iterator().next();
				if (selectedContentObject != null && !selectedContentObject.isReadOnly())
					changeRegionType.setEnabled(selectedContentObject.getType() != null && selectedContentObject.getType() instanceof RegionType);
				else
					changeRegionType.setEnabled(false);
			}
		} catch (Exception exc) {
			logManager.logError(ERROR_ON_SELECTION_CHANGE, "Error on page content object selection change");
			exc.printStackTrace();
		}
	}

	/**
	 * Creates the dialogue to view/edit text content.
	 */
	private void createRegionTypeDialog() {
		regionTypeDialog = new DialogBox();
		regionTypeDialog.setModal(false);
		try {
			regionTypeDialog.ensureDebugId("cwDialogBox"); 
			regionTypeDialog.setText("Change Region Type"); 
			
			VerticalPanel dialogContents = new VerticalPanel();
			dialogContents.setSpacing(4);
			regionTypeDialog.setWidget(dialogContents);
	
			RegionTypeEditor typeEditor = new RegionTypeEditor(selectionManager);
			selectionManager.addListener(typeEditor);
			typeEditor.addRegionTypeSelectionListener(this);
			
			dialogContents.add(typeEditor.getWidget());
			
		    // Add buttons at the bottom of the dialog
			HorizontalPanel textDlgbuttons = new HorizontalPanel();
			textDlgbuttons.setSpacing(5);
			dialogContents.add(textDlgbuttons);
		    dialogContents.setCellHorizontalAlignment(textDlgbuttons, HasHorizontalAlignment.ALIGN_RIGHT);
		    
		    Button closeButton = new Button("Close", new ClickHandler() { 
		    	public void onClick(ClickEvent event) {
		    		regionTypeDialog.hide();
		        }
		    });
		    textDlgbuttons.add(closeButton);
		} catch (Exception exc) {
			logManager.logError(ERROR_CREATING_REGION_TYPE_DIALOG, "Error displaying the region type dialogue");
			exc.printStackTrace();
		}
	}

	@Override
	public void regionTypeSelected(RegionType selectedType) {
		try {
			textChanged = false;
			
			//Region selected
			if (selectionManager.getSelection().size() == 1 
					&& (selectionManager.getSelection().iterator().next().getType() instanceof RegionType)) {
				ContentObjectC selObj = selectionManager.getSelection().iterator().next();
	
				pageSync.syncRegionType(selObj, selectedType, null);
			} 
			else { //No region selected -> Activate create tool
				defaultRegionType = selectedType;
				//Switch to regions if necessary
				/*if (!"Region".equals(currentPageObjectType)) { 
					loadPageContent("Region"); 
				}
				
				//Activate tool
				if (selectedType == null)
					selectedType = RegionType.UnknownRegion;
				CreatePageObjectTool tool = new CreatePageObjectTool(DrawingTool.Rectangle, pageView, selectedType, pageSync);
				tool.setRegionSubType(selectedSubType);
				*/
			}
		} catch (Exception exc) {
			logManager.logError(ERROR_REGION_TYPE_SELECTION_CHANGE, "Error in region type selection event");
			exc.printStackTrace();
		}
	}

	/**
	 * Shows an error message at the top of the page (can be closed; auto hide after 10 seconds)
	 * @param message Error message to display
	 */
	private void showErrorDialogue(String message) {
		try {
			if (message == null || message.isEmpty())
				return;
			
			if (errorMessageBox == null) {
				errorMessageBox = new DialogBox(false, false);
				errorMessageBox.getElement().setId("errorMessageBoxDialog");
				
				errorMessageBox.addStyleName("errorMessageBox");
			
				final HorizontalPanel panel = new HorizontalPanel();
				errorMessageBox.add(panel);
				panel.addStyleName("errorMessagePanel");
			
				errorMessage = new Label(); 
				panel.add(errorMessage);
				errorMessage.getElement().getStyle().setMarginRight(5, Unit.PX);
			
				//Close button
				Image img = new Image("img/close.png"); 
				PushButton buttonClose = new PushButton(img); 
				panel.add(buttonClose);
				buttonClose.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						errorMessageBox.hide();
					}
				});
			
				verticalPanel.add(errorMessageBox);
			}
			errorMessage.setText(message);
			
			//Hide after 10 seconds (can be changed in AppConstants.properties)
			if (errorMessageTimer != null)
				errorMessageTimer.cancel();
			errorMessageTimer = new Timer() {
				public void run() {
					errorMessageBox.hide();
				}
			};
			errorMessageTimer.schedule(10000);
			
			errorMessageBox.showRelativeTo(toolbar);
		} catch (Exception exc) {
			logManager.logError(ERROR_UNSPECIFIED, "Error displaying the message dialogue");
			exc.printStackTrace();
		}
	}
	
	@Override
	public void textChanged() {
		textChanged = true;
	}
	
	@Override
	public void preSelectionHandlingOfTextContentView(SelectionManager manager) {
		try {
			if (textChanged && manager != null && manager.getPreviousSelection().size() == 1) {
				ContentObjectC previouslySelectedObject = manager.getPreviousSelection().iterator().next();
				//Save changed text
				if (previouslySelectedObject != null && !previouslySelectedObject.isReadOnly()) {
					saveTextContent(previouslySelectedObject);
				}
			}
		} catch (Exception exc) {
			logManager.logError(ERROR_UNSPECIFIED, "Error in preSelectionHandlingOfTextContentView()");
			exc.printStackTrace();
		}
	}
	
	/**
	 * Applies the text from the text dialogue to the currently selected object.
	 */
	/*private void saveTextContent() {
		try {
			Set<ContentObjectC> selObjects = selectionManager.getSelection();
			if (selObjects.size() == 1) {
				saveTextContent(selObjects.iterator().next());
			}
		} catch (Exception exc) {
			logManager.logError(ERROR_UNSPECIFIED, "Error saving changed text content");
			exc.printStackTrace();
		}
	}*/
	
	/**
	 * Applies the text from the text dialogue to the specified object.
	 */
	private void saveTextContent(ContentObjectC obj) {
		try {
			boolean canEdit = false;
			Permissions permissions = userManager.getPermissions();
			if (permissions != null) {
				canEdit = permissions.isPermitted(DefaultPermissionNames.Edit);
			}
			if (!canEdit)
				return;
			
			obj.setText(textContentView.getText());
			pageSync.syncTextContent(obj);
			textChanged = false;
		} catch (Exception exc) {
			logManager.logError(ERROR_UNSPECIFIED, "Error saving changed text content");
			exc.printStackTrace();
		}
	}

	@Override
	public void contentObjectPropertyClicked(ClickEvent event, int cellLeft, int cellBottom, ContentObjectC object, Variable property) {

		showPropertyEditorDialog(property, propertiesView.getWidget().getAbsoluteLeft(), cellBottom);
	}

	@Override
	public void propertyViewHeadingClicked(ClickEvent event, ContentObjectC object) {
		if (object != null) {
			regionTypeDialog.center();
			regionTypeDialog.show();
		}
	}
	
	/**
	 * Displays a dialogue to edit a single page content object attribute 
	 */
	private void showPropertyEditorDialog(Variable attr, int left, int top) {
		try {
			if (propertyEditorDialog == null) {
				propertyEditorDialog = new DialogBox(true);
				propertyEditorDialog.setAnimationEnabled(false);

				propertyEditorDialog.ensureDebugId("cwDialogBox"); 
				//propertyEditorDialog.setText("Edit Attribute"); 
				
				VerticalPanel dialogContents = new VerticalPanel();
				dialogContents.setSpacing(4);
				propertyEditorDialog.setWidget(dialogContents);
		
				attributeEditor = new AttributeEditor();
				dialogContents.add(attributeEditor.getWidget());
				
			    // Add buttons at the bottom of the dialog
				HorizontalPanel dlgbuttons = new HorizontalPanel();
				dlgbuttons.setSpacing(5);
				dialogContents.add(dlgbuttons);
			    dialogContents.setCellHorizontalAlignment(dlgbuttons, HasHorizontalAlignment.ALIGN_CENTER);
			    
			    Button closeButton = new Button("Apply", new ClickHandler() { 
			    	public void onClick(ClickEvent event) {
			    		propertyEditorDialog.hide();
			    		applyAttribute();
			        }
			    });
			    dlgbuttons.add(closeButton);

			    Button cancelButton = new Button("Cancel", new ClickHandler() { 
			    	public void onClick(ClickEvent event) {
			    		propertyEditorDialog.hide();
			        }
			    });
			    dlgbuttons.add(cancelButton);
			    
			    attributeEditor.setKeyPressHandler(new KeyPressHandler() {
					@Override
					public void onKeyPress(KeyPressEvent event) {
						//ENTER?
						if (KeyCodes.KEY_ENTER == event.getNativeEvent().getKeyCode()) {
							propertyEditorDialog.hide();
							applyAttribute();
						}
					}
				});
			    /*propertyEditorDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
					
					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
					}
				});*/
			}
			
			attributeEditor.update(attr);
			propertyEditorDialog.setPopupPosition(left, top);
			//propertyEditorDialog.setWidth("400px");
			attributeEditor.getWidget().getElement().getStyle().setWidth(propertiesView.getWidget().getOffsetWidth() - 40, Unit.PX);
			propertyEditorDialog.show();
			attributeEditor.focus();
		} catch (Exception exc) {
			logManager.logError(ERROR_UNSPECIFIED, "Error displaying the attribute editor dialogue");
			exc.printStackTrace();
		}
	}
	
	private void applyAttribute() {
		try {
			if (selectionManager.getSelection().size() == 1) {
				Variable attr = attributeEditor.getAttribute(); 
				attr.setValue(attributeEditor.getNewValue());
				ContentObjectC obj = selectionManager.getSelection().iterator().next();
				pageSync.syncAttribute(obj, attr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		propertiesView.selectionChanged(selectionManager);
	}

	@Override
	public void attributeSynchronized(ContentObjectC object) {
	}

	@Override
	public void attributeSyncFailed(ContentObjectC object, Throwable caught) {
		showErrorDialogue("Attribute not synchronised.");
	}

	@Override
	public void readingOrderLoaded() {
		if (readingOrderRendererPlugin.isEnabled())
			pageView.getRenderer().refresh();
	}

	@Override
	public void readingOrderLoadingFailed(Throwable caught) {
		showErrorDialogue("Attribute not synchronised.");
	}

	@Override
	public void pageSizeReceived(Dimension pageSize) {
		if (pageSize != null && !imageLoaded) {
			try {
				pageLayout.setWidth(pageSize.width);
				pageLayout.setHeight(pageSize.height);
				pageView.zoomToFitPage();
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		}
	}

	@Override
	public void getPageSizeFailed(Throwable caught) {
		//showErrorDialogue("Page size could not be retrieved.");
	}
}
