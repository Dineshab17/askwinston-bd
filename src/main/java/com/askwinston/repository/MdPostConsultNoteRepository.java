package com.askwinston.repository;

import com.askwinston.model.MdPostConsultNote;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MdPostConsultNoteRepository extends CrudRepository<MdPostConsultNote, Long> {
}
