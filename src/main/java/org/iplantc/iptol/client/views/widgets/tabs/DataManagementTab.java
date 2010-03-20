package org.iplantc.iptol.client.views.widgets.tabs;

import gwtupload.client.IUploader;
import org.iplantc.iptol.client.IptolClientConstants;
import org.iplantc.iptol.client.IptolDisplayStrings;
import org.iplantc.iptol.client.dialogs.FileUploadDialog;
import org.iplantc.iptol.client.views.widgets.panels.DataManagementGridPanel;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuBar;
import com.extjs.gxt.ui.client.widget.menu.MenuBarItem;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;

public class DataManagementTab extends WorkspaceTab 
{
	private VerticalPanel panel;
	private FileUploadDialog dlgUpload;	
	private DataManagementGridPanel pnlDataManagementGrid;
	private IptolClientConstants constants = (IptolClientConstants)GWT.create(IptolClientConstants.class);
	private static IptolDisplayStrings displayStrings = (IptolDisplayStrings) GWT.create(IptolDisplayStrings.class);
	
	//////////////////////////////////////////
	//constructor
	public DataManagementTab(String idWorkspace,HandlerManager eventbus) 
	{
		super(idWorkspace,displayStrings.dataManagement(),eventbus,Type.DATA_MANAGEMENT);
	}

	//////////////////////////////////////////
	//private methods
	private void doUpload(int dlgX,int dlgY)
	{
		IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler() 
		{
			public void onFinish(IUploader uploader) 
			{
				if(dlgUpload != null) 
				{
					dlgUpload.hide();
				}				
			}
		};
				
		dlgUpload = new FileUploadDialog(displayStrings.uploadYourData(),constants.fileUploadServlet(),new Point(dlgX,dlgY),onFinishUploaderHandler);
		dlgUpload.setModal(true);
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
		
		//upload menu item
		item = new MenuItem(displayStrings.upload(),new SelectionListener<MenuEvent>() 
		{
			@Override
			public void componentSelected(MenuEvent ce) 
			{
				final int  DIALOG_PADX = 20;
				final int  DIALOG_PADY = 10;
				
				doUpload(panel.getAbsoluteLeft() + DIALOG_PADX,panel.getAbsoluteTop() + DIALOG_PADY);
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
	//protected methods
	@Override
	protected void buildTabBody() 
	{	
		add(buildMenuBar());
		
		panel = new VerticalPanel();
		panel.setSpacing(15);
		
		pnlDataManagementGrid = new DataManagementGridPanel(idWorkspace,displayStrings.availableFiles(),eventbus); 
		panel.add(pnlDataManagementGrid);
		
		add(panel);		
	}
}
