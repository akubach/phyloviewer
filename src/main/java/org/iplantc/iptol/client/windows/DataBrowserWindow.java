package org.iplantc.iptol.client.windows;

import java.util.HashMap;

import org.iplantc.iptol.client.IptolClientConstants;
import org.iplantc.iptol.client.dialogs.IPlantDialog;
import org.iplantc.iptol.client.dialogs.ImportDialog;
import org.iplantc.iptol.client.dialogs.panels.FileUploadPanel;
import org.iplantc.iptol.client.events.DefaultUploadCompleteHandler;
import org.iplantc.iptol.client.events.UploadCompleteHandler;
import org.iplantc.iptol.client.views.widgets.panels.DataManagementGridPanel;

import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuBar;
import com.extjs.gxt.ui.client.widget.menu.MenuBarItem;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;

public class DataBrowserWindow extends IplantWindow
{
	//////////////////////////////////////////
	//private variables
	private String idWorkspace;
	private IPlantDialog dlgUpload;
	private DataManagementGridPanel pnlDataManagementGrid;
		
	//////////////////////////////////////////
	//constructor
	public DataBrowserWindow(String tag,String idWorkspace)
	{
		super(tag);
		this.idWorkspace = idWorkspace;
		
		setHeading(displayStrings.myData());
		setClosable(true);
		setResizable(false);
		setMaximizable(false);
		setMinimizable(true);
		setWidth(740);
		setHeight(380);	
		setIcon(IconHelper.createStyle("bogus"));
	}
	
	//////////////////////////////////////////
	//private methods
	private void promptUpload(final String idParent,Point p)
	{	
		// provide key/value pairs for hidden fields
		HashMap<String, String> hiddenFields = new HashMap<String, String>();
		hiddenFields.put(FileUploadPanel.HDN_WORKSPACE_ID_KEY, idWorkspace);
		hiddenFields.put(FileUploadPanel.HDN_PARENT_ID_KEY, idParent);		

		// define a handler for upload completion
		UploadCompleteHandler handler = new DefaultUploadCompleteHandler(idParent) 
		{ 
			@Override   
			public void onAfterCompletion() 
			{
				if(dlgUpload != null) 
				{
					dlgUpload.hide();
				}
			}
		};	
		
		// get the servlet action url
		IptolClientConstants constants = (IptolClientConstants)GWT.create(IptolClientConstants.class);
		String servletActionUrl = constants.fileUploadServlet();
		
		FileUploadPanel pnlUpload = new FileUploadPanel(hiddenFields, servletActionUrl, handler);
		
		dlgUpload = new IPlantDialog(displayStrings.uploadYourData(), 375, pnlUpload);
        dlgUpload.setButtons(Dialog.CANCEL);
        dlgUpload.setPagePosition(p);
        dlgUpload.show();	
	}
	
	//////////////////////////////////////////
	private void doCreateFolder()
	{
		if(pnlDataManagementGrid != null)
		{
			pnlDataManagementGrid.promptForFolderCreate();
		}		
	}

	//////////////////////////////////////////
	private void promptForImport(Point p)
	{
		String idFolder = pnlDataManagementGrid.getUploadParentId();
		
		//do we have an item selected?
		if(idFolder != null)
		{
			ImportDialog dlg = new ImportDialog(p,idWorkspace,idFolder);
			dlg.show();
		}
	}
	
	//////////////////////////////////////////
	private MenuBarItem buildHelpMenu()
	{
		Menu menu = new Menu();  
	    
		
		MenuItem helpContent = new MenuItem(displayStrings.helpContent(),new SelectionListener<MenuEvent>() 
				{
					@Override
					public void componentSelected(MenuEvent ce) 
					{
						doHelpContentDisplay();
					}
				});
		
		MenuItem item = new MenuItem(displayStrings.about(),new SelectionListener<MenuEvent>() 
		{
			@Override
			public void componentSelected(MenuEvent ce) 
			{
				doAboutDisplay();
			}
		});
		
		menu.add(helpContent);
		menu.add(item);
				
		return new MenuBarItem(displayStrings.help(),menu);
	}
	
	//////////////////////////////////////////
	private MenuItem buildImportMenuItem()
	{
		MenuItem ret = new MenuItem(displayStrings.tagImport());
		Menu sub = new Menu();
		
		MenuItem item = new MenuItem(displayStrings.phylota());
		item.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			@Override
			public void componentSelected(MenuEvent ce)
			{
				promptForImport(ce.getXY());
			}
		});
		
		//add our item to our sub-menu
		sub.add(item);
		ret.setSubMenu(sub);
		
		return ret;	
	}	
	//////////////////////////////////////////
	private MenuBarItem buildFileMenu()
	{
		Menu menu = new Menu();  
		
		MenuItem item = new MenuItem(displayStrings.tagNew());  
		menu.add(item);  
		
		//new folder menu item       
		Menu sub = new Menu();  
		sub.add(new MenuItem(displayStrings.folder(),new SelectionListener<MenuEvent>() 
		{
			@Override
			public void componentSelected(MenuEvent ce) 
			{
				doCreateFolder();
			}
		}));  
		
		item.setSubMenu(sub);  
		
		//import menu item
		menu.add(buildImportMenuItem());
		
		//upload menu item
		item = new MenuItem(displayStrings.upload(),new SelectionListener<MenuEvent>() 
		{
			@Override
			public void componentSelected(MenuEvent ce) 
			{
				String parentId = pnlDataManagementGrid.getUploadParentId();
				promptUpload(parentId,ce.getXY());
			}
		});
		
		menu.add(item);
					
		return new MenuBarItem(displayStrings.file(),menu);
	}
			
	//////////////////////////////////////////
	private MenuBar buildMenuBar()
	{
		MenuBar ret = new MenuBar();  
		
		ret.setBorders(true);  
		ret.setStyleAttribute("borderTop","none");  
		ret.add(buildFileMenu());
		ret.add(buildHelpMenu());
		
		return ret;
	}

	//////////////////////////////////////////
	private void doAboutDisplay()
	{
		com.google.gwt.user.client.Window.open("help/about.html",displayStrings.about(),null);
	}
	
	//////////////////////////////////////////
	private void doHelpContentDisplay()  
	{
		com.google.gwt.user.client.Window.open("help/mydata.html",displayStrings.help(),null);		
	}
	
	//////////////////////////////////////////
	//protected methods
	@Override  
	protected void onRender(Element parent,int index) 
	{  
		super.onRender(parent,index);
		
		add(buildMenuBar());
		
		VerticalPanel panel = new VerticalPanel();
		panel.setSpacing(15);
		
		pnlDataManagementGrid = new DataManagementGridPanel(idWorkspace,displayStrings.availableFiles()); 
		panel.add(pnlDataManagementGrid);
		
		add(panel);		
	}

	//////////////////////////////////////////
	//public methods
	@Override
	public void cleanup() 
	{
		//we have no cleanup		
	}	
}
