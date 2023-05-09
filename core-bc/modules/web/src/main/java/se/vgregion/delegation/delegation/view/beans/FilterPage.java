package se.vgregion.delegation.delegation.view.beans;

import lombok.Data;

import java.util.List;

@Data
public class FilterPage<QueryType, ResultType> {

    private QueryType query;

    private List<ResultType> result;

    private Integer page;

    private Integer pageSize;

}
