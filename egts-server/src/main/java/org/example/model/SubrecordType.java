package org.example.model;

import lombok.Getter;

import java.util.Optional;

@Getter
public enum SubrecordType {
    POS_Data(16),
    TERM_IDENTITY(1),
    RESPONSE(0),
    RESULT_CODE(9),
    EXT_POS_DATA(17),
    AD_SENSORS_DATA(18),
    STATE_DATA(21),
    LIQUID_LEVEL_SE(27),
    ABS_CNTR_DATA(25),
    AUTH_INFO(7),
    COUNTERS_DATA(19),
    EGTS_PLUS_DATA_TYPE(15),
    ABS_AN_SENS_DATA(24);

    private Integer id;

    SubrecordType(Integer id) {
        this.id = id;
    }

    public static Optional<SubrecordType> fromId(Integer id) {
        for (SubrecordType value : SubrecordType.values()) {
            if (value.getId().equals(id)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }
}
