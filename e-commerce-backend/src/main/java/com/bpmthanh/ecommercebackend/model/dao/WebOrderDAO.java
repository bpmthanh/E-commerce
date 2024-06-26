package com.bpmthanh.ecommercebackend.model.dao;

import com.bpmthanh.ecommercebackend.model.LocalUser;
import com.bpmthanh.ecommercebackend.model.WebOrder;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

/**
 * Data Access Object to access WebOrder data.
 */
public interface WebOrderDAO extends ListCrudRepository<WebOrder, Long> {

    List<WebOrder> findByUser(LocalUser user);

}