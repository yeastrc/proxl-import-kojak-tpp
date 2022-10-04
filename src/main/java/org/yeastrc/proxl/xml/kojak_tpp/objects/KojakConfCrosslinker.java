package org.yeastrc.proxl.xml.kojak_tpp.objects;

import java.math.BigDecimal;
import java.util.Collection;

public class KojakConfCrosslinker {

    @Override
    public String toString() {
        return "KojakConfCrosslinker{" +
                "name='" + name + '\'' +
                ", linkableEnd1=" + linkableEnd1 +
                ", linkableEnd2=" + linkableEnd2 +
                ", isCleavableLinker=" + isCleavableLinker +
                ", crosslinkMass=" + crosslinkMass +
                ", monolinkMasses=" + monolinkMasses +
                ", cleavageProductMasses=" + cleavageProductMasses +
                '}';
    }

    public KojakConfCrosslinker(String name, KojakConfCrosslinkerLinkableEnd linkableEnd1, KojakConfCrosslinkerLinkableEnd linkableEnd2, boolean isCleavableLinker, BigDecimal crosslinkMass, Collection<BigDecimal> monolinkMasses, Collection<BigDecimal> cleavageProductMasses) {
        this.name = name;
        this.linkableEnd1 = linkableEnd1;
        this.linkableEnd2 = linkableEnd2;
        this.isCleavableLinker = isCleavableLinker;
        this.crosslinkMass = crosslinkMass;
        this.monolinkMasses = monolinkMasses;
        this.cleavageProductMasses = cleavageProductMasses;
    }

    public String getName() {
        return name;
    }

    public KojakConfCrosslinkerLinkableEnd getLinkableEnd1() {
        return linkableEnd1;
    }

    public KojakConfCrosslinkerLinkableEnd getLinkableEnd2() {
        return linkableEnd2;
    }

    public boolean isCleavableLinker() {
        return isCleavableLinker;
    }

    public BigDecimal getCrosslinkMass() {
        return crosslinkMass;
    }

    public Collection<BigDecimal> getMonolinkMasses() {
        return monolinkMasses;
    }

    public Collection<BigDecimal> getCleavageProductMasses() {
        return cleavageProductMasses;
    }

    private String name;
    private KojakConfCrosslinkerLinkableEnd linkableEnd1;
    private KojakConfCrosslinkerLinkableEnd linkableEnd2;
    private boolean isCleavableLinker;
    private BigDecimal crosslinkMass;
    private Collection<BigDecimal> monolinkMasses;
    private Collection<BigDecimal> cleavageProductMasses;

}
