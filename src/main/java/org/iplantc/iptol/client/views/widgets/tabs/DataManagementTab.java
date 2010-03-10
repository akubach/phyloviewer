package org.iplantc.iptol.client.views.widgets.tabs;

import org.iplantc.iptol.client.dialogs.FileUploadDialog;
import org.iplantc.iptol.client.views.widgets.panels.DataManagementGridPanel;
import org.iplantc.iptol.client.views.widgets.panels.RawDataPanel;
import gwtupload.client.IUploader;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuBar;
import com.extjs.gxt.ui.client.widget.menu.MenuBarItem;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.event.shared.HandlerManager;

public class DataManagementTab extends WorkspaceTab 
{
	private VerticalPanel panel;
	private RawDataPanel panelData;
	private FileUploadDialog dlgUpload;
	
	//////////////////////////////////////////
	//constructor
	public DataManagementTab(String idWorkspace,HandlerManager eventbus) 
	{
		super(idWorkspace,"Data Mgmt.",eventbus,Type.DATA_MANAGEMENT);
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
				
		dlgUpload = new FileUploadDialog("Upload your data","servlet.gupld",new Point(dlgX,dlgY),onFinishUploaderHandler);
		dlgUpload.setModal(true);
		dlgUpload.show();
	}
		
	//////////////////////////////////////////
	private void doImport()
	{
		MessageBox.alert("Coming soon!","Import screen.",null);
	}
	
	//////////////////////////////////////////
	private void doCreateFolder()
	{
		MessageBox.alert("Coming soon!","Folder creation.",null);
	}
	
	//////////////////////////////////////////
	private MenuBarItem buildFileMenu()
	{
		Menu menu = new Menu();  
		
		MenuItem item = new MenuItem("New");  
		menu.add(item);  
		
		//new folder menu item       
		Menu sub = new Menu();  
		sub.add(new MenuItem("Folder",new SelectionListener<MenuEvent>() 
		{
			@Override
			public void componentSelected(MenuEvent ce) 
			{
				doCreateFolder();
			}
		}));  
		
		item.setSubMenu(sub);  
		
		//upload menu item
		item = new MenuItem("Upload",new SelectionListener<MenuEvent>() 
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
		
		//import menu item
		item = new MenuItem("Import",new SelectionListener<MenuEvent>() 
		{
			@Override
			public void componentSelected(MenuEvent ce) 
			{
				doImport();
			}
		});
		
		menu.add(item);
			
		return new MenuBarItem("File",menu);
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
		panel.add(new DataManagementGridPanel(idWorkspace,"Available Files",eventbus));
		
		add(panel);		
	}
	
	//////////////////////////////////////////
	//public methods
	public void displayRawData(String filename)
	{
		if(panelData == null)
		{
			panelData = new RawDataPanel(eventbus,filename);
			panel.add(panelData);
			
			layout();
		}				
	}	
}
