package org.example.doantn.Dto.response;

public class BatchDto {
    private String name;

    public BatchDto() {
    }

    public BatchDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Có thể thêm các trường khác nếu cần trả về thêm thông tin
}