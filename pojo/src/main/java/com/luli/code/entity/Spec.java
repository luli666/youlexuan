package com.luli.code.entity;

import com.luli.code.pojo.Specification;
import com.luli.code.pojo.SpecificationOption;

import java.io.Serializable;
import java.util.List;

public class Spec implements Serializable {
    private Specification specification;
    private List<SpecificationOption> specificationOptionList ;

    public Spec() {
    }

    public Specification getSpecification() {
        return specification;
    }

    public void setSpecification(Specification specification) {
        this.specification = specification;
    }

    public List<SpecificationOption> getSpecificationOptionList() {
        return specificationOptionList;
    }

    public void setSpecificationOptionList(List<SpecificationOption> specificationOptionList) {
        this.specificationOptionList = specificationOptionList;
    }
}
