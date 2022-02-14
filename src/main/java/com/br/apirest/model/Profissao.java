package com.br.apirest.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Profissao implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String nomeProfissao;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNomeProfissao() {
		return nomeProfissao;
	}

	public void setNomeProfissao(String nomeProfissao) {
		this.nomeProfissao = nomeProfissao;
	}
	
	

}
