package com.askwinston.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MdPostConsultNoteDto {

    @NotNull(message = "Doctor must counsel patient on side effects")
    @Min(value = 1, message = "Doctor must counsel patient on side effects")
    @Max(value = 1, message = "Doctor must counsel patient on side effects")
    private int counseledOnSideEffects;

    @NotNull(message = "Doctor must counsel patient on symptoms for emergency")
    @Min(value = 1, message = "Doctor must counsel patient on symptoms for emergency")
    @Max(value = 1, message = "Doctor must counsel patient on symptoms for emergency")
    private int counseledOnSymptomsForEmergency;

    @NotNull(message = "Doctor must address all patients concerns")
    @Min(value = 1, message = "Doctor must address all patients concerns")
    @Max(value = 1, message = "Doctor must address all patients concerns")
    private int patientsConcernsAddressed;

    @NotNull(message = "Doctor must answer whether patient have requested a counsel by Pharmacy")
    @Min(value = 0, message = "Value must be 0 or 1")
    @Max(value = 1, message = "Value must be 0 or 1")
    private int requestedCounselByPharmacy;

    @Size(max = 4047, message = "Exceeded max allowed length for notes (4047 chars)")
    private String doctorsCustomNotes;

}
