package org.iplantc.phyloviewer.client.events;

import org.iplantc.phyloviewer.shared.model.IDocument;

import com.google.gwt.event.shared.HandlerRegistration;

public interface HasDocument
{
	public void setDocument(IDocument document);
	public IDocument getDocument();
	public HandlerRegistration addDocumentChangeHandler(DocumentChangeHandler handler);
}
