package org.iplantc.iptol.client.views.widgets.panels;

import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Element;

public class RawDataPanel extends VerticalPanel 
{
	//////////////////////////////////////////
	//private variables
	private HandlerManager eventbus;
	private String caption;

	//////////////////////////////////////////
	//constructor
	public RawDataPanel(HandlerManager eventbus,String caption)
	{
		this.eventbus = eventbus;
		this.caption = caption;
	}
	
	//////////////////////////////////////////
	//private methods	
	private TextArea buildDataTextArea()
	{
		TextArea ret = new TextArea();
		
		ret.setStyleName("iptolcaptionlabel");
		
		ret.setHideLabel(true);
		ret.setReadOnly(true);
		ret.setValue("#NEXUS\n\n" +
				"BEGIN TAXA;\n" +
				"\tdimensions ntax=4;\n" +
				"\ttaxlabels A B C D;\n" +
				"END;\n\n" +
				"BEGIN CHARACTERS;\n" +
				"\tdimensions nchar=5;\n" +
				"\tformat datatype=protein gap=-;\n" +
				"\tcharlabels 1 2 3 4 Five;\n" +
				"\tmatrix\n" +
				"A    MA-LL\n" +
				"B    MA-LE\n" +
				"C    MEATY\n" +
				"D    ME-TE;\n" +
				"END;\n\n" +				
				"BEGIN TREES;\n" +
				"\ttree \"basic bush\" = ((A:1,B:1):1,(C:1,D:1):1);\n" +
				"END;\n");
		
		ret.setWidth(410);
		ret.setHeight(250);
		return ret;
	}
			
	//////////////////////////////////////////
	//protected methods
	@Override
	protected void onRender(Element parent, int index) 
	{  
		super.onRender(parent, index);
		
		VerticalPanel panel = new VerticalPanel();		
		panel.setSpacing(5);		
		
		TextArea dataView = buildDataTextArea();  
		panel.add(dataView);
						
		FieldSet fieldSet = new FieldSet();  
		fieldSet.setLayout(new FlowLayout(10));  
		fieldSet.setStyleName("iptolcaptionlabel");
		fieldSet.setHeading(caption);
		
		fieldSet.add(panel);
		add(fieldSet);		
	}
}
