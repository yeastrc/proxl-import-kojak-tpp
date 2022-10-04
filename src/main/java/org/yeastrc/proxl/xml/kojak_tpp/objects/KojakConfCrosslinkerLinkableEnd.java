package org.yeastrc.proxl.xml.kojak_tpp.objects;

import java.util.Collection;

public class KojakConfCrosslinkerLinkableEnd {

    @Override
    public String toString() {
        return "KojakConfCrosslinkerLinkableEnd{" +
                "linkableResidues=" + linkableResidues +
                ", linksProteinNTerminus=" + linksProteinNTerminus +
                ", linksProteinCTerminus=" + linksProteinCTerminus +
                '}';
    }

    public KojakConfCrosslinkerLinkableEnd(Collection<String> linkableResidues, boolean linksProteinNTerminus, boolean linksProteinCTerminus) {
        this.linkableResidues = linkableResidues;
        this.linksProteinNTerminus = linksProteinNTerminus;
        this.linksProteinCTerminus = linksProteinCTerminus;
    }

    public Collection<String> getLinkableResidues() {
        return linkableResidues;
    }

    public boolean isLinksProteinNTerminus() {
        return linksProteinNTerminus;
    }

    public boolean isLinksProteinCTerminus() {
        return linksProteinCTerminus;
    }

    private Collection<String> linkableResidues;
    private boolean linksProteinNTerminus;
    private boolean linksProteinCTerminus;
}
