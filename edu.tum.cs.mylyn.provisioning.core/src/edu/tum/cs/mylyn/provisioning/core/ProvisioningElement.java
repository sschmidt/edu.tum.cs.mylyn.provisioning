package edu.tum.cs.mylyn.provisioning.core;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.mylyn.context.core.IInteractionElement;

/**
 * A provisioning element represents an interesting platform element (e.g. a git
 * repository, a breakpoint, ...). Provisioning elements are stored in
 * provisioning contexts. If the corresponding platform element is not available
 * in the current workspace, a user might request to provision (e.g. clone the
 * git repository, import the breakpoint) a provisioning element.
 * 
 * @author Sebastian Schmidt <sebastian.schmidt@tum.de>
 * @noimplement
 * @noextend
 */
public class ProvisioningElement {

	private final String contentType;
	private final String identifier;
	private final boolean relatedToInteractionContext;
	private final Set<IInteractionElement> interactions = new HashSet<IInteractionElement>();

	public ProvisioningElement(String contentType, String identifier) {
		this.contentType = contentType;
		this.identifier = identifier;
		this.relatedToInteractionContext = false;
	}

	public ProvisioningElement(String contentType, String identifier, boolean relatedToInteractionContext) {
		this.contentType = contentType;
		this.identifier = identifier;
		this.relatedToInteractionContext = relatedToInteractionContext;
	}

	public String getContentType() {
		return contentType;
	}

	public String getIdentifier() {
		return identifier;
	}

	public boolean isRelatedToInteractionContext() {
		return relatedToInteractionContext;
	}

	public void addRelatedInteraction(IInteractionElement element) {
		interactions.add(element);
	}

	public Set<IInteractionElement> getRelatedInteractions() {
		return interactions;
	}

	public void removeRelatedInteractions(Set<IInteractionElement> interactions) {
		interactions.removeAll(interactions);
	}

	public void addRelatedInteractions(Set<IInteractionElement> interactions) {
		interactions.addAll(interactions);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((contentType == null) ? 0 : contentType.hashCode());
		result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProvisioningElement other = (ProvisioningElement) obj;
		if (contentType == null) {
			if (other.contentType != null)
				return false;
		} else if (!contentType.equals(other.contentType))
			return false;
		if (identifier == null) {
			if (other.identifier != null)
				return false;
		} else if (!identifier.equals(other.identifier))
			return false;
		return true;
	}
}
