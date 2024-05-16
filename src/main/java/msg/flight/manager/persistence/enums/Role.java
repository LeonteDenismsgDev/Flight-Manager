package msg.flight.manager.persistence.enums;

public enum Role {
    CREW_ROLE("cr"), FLIGHT_MANAGER_ROLE("fm"), COMPANY_MANAGER_ROLE("cm"), ADMINISTRATOR_ROLE("ad");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
