package com.br.apirest.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.br.apirest.model.Telefone;

@Repository
public interface TelefoneRepository extends CrudRepository<Telefone, Long> {
	

}
