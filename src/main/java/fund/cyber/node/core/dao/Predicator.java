package fund.cyber.node.core.dao;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public interface Predicator<T> {
    Predicate predicate(CriteriaBuilder builder, Root<T> root);
}
