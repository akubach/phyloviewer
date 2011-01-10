package org.iplantc.phyloviewer.client.events;

import org.iplantc.phyloviewer.shared.model.IDocument;

import com.google.gwt.event.shared.GwtEvent;

public class DocumentChangeEvent extends GwtEvent<DocumentChangeHandler>
{
	public static final Type<DocumentChangeHandler> TYPE = new Type<DocumentChangeHandler>();
	private IDocument document;
	
	public DocumentChangeEvent(IDocument document)
	{
		this.document = document;
	}
	
	@Override
	protected void dispatch(DocumentChangeHandler handler)
	{
		handler.onDocumentChange(this);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<DocumentChangeHandler> getAssociatedType()
	{
		return TYPE;
	}

	public IDocument getDocument()
	{
		return document;
	}


}
