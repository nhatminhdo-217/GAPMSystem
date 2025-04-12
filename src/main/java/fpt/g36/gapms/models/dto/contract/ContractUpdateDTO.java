package fpt.g36.gapms.models.dto.contract;

public class ContractUpdateDTO {
    private String name;

    private String path;

    public ContractUpdateDTO() {
    }

    public ContractUpdateDTO(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
