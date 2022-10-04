package org.yeastrc.proxl.xml.kojak_tpp.objects;

import java.math.BigDecimal;
import java.util.Collection;

public class KojakConfCrosslinkerBuilder {
    private String name;
    private KojakConfCrosslinkerLinkableEnd linkableEnd1;
    private KojakConfCrosslinkerLinkableEnd linkableEnd2;
    private boolean isCleavableLinker;
    private BigDecimal crosslinkMass;
    private Collection<BigDecimal> monolinkMasses;
    private Collection<BigDecimal> cleavageProductMasses;

    public KojakConfCrosslinkerBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public KojakConfCrosslinkerBuilder setLinkableEnd1(KojakConfCrosslinkerLinkableEnd linkableEnd1) {
        this.linkableEnd1 = linkableEnd1;
        return this;
    }

    public KojakConfCrosslinkerBuilder setLinkableEnd2(KojakConfCrosslinkerLinkableEnd linkableEnd2) {
        this.linkableEnd2 = linkableEnd2;
        return this;
    }

    public KojakConfCrosslinkerBuilder setIsCleavableLinker(boolean isCleavableLinker) {
        this.isCleavableLinker = isCleavableLinker;
        return this;
    }

    public KojakConfCrosslinkerBuilder setCrosslinkMass(BigDecimal crosslinkMass) {
        this.crosslinkMass = crosslinkMass;
        return this;
    }

    public KojakConfCrosslinkerBuilder setMonolinkMasses(Collection<BigDecimal> monolinkMasses) {
        this.monolinkMasses = monolinkMasses;
        return this;
    }

    public KojakConfCrosslinkerBuilder setCleavageProductMasses(Collection<BigDecimal> cleavageProductMasses) {
        this.cleavageProductMasses = cleavageProductMasses;
        return this;
    }

    public KojakConfCrosslinker createKojakConfCrosslinker() {
        return new KojakConfCrosslinker(name, linkableEnd1, linkableEnd2, isCleavableLinker, crosslinkMass, monolinkMasses, cleavageProductMasses);
    }
}