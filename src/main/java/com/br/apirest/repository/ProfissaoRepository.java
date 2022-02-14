package com.br.apirest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.br.apirest.model.Profissao;

@Repository
public interface ProfissaoRepository extends JpaRepository<Profissao, Long> {

}
