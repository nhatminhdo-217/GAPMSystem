package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.models.entities.Rfq;
import fpt.g36.gapms.models.entities.RfqDetail;
import fpt.g36.gapms.repositories.RfqDetailRepository;
import fpt.g36.gapms.services.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class RfqDetailServiceImpl implements RfqDetailService {

    @Autowired
    private RfqDetailRepository  rfqDetailRepository;
    @Autowired
    private RfqService rfqService;
    @Autowired
    private ProductService productService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Override
    public void saveRfqDetail(List<RfqDetail> rfqDetails) {
        rfqDetailRepository.saveAll(rfqDetails);
    }

    @Override
    public void deleteRfqDetailById(Long id) {
        rfqDetailRepository.deleteById(id);
    }


    @Override
    public RfqDetail getRfqDetailById(Long rfqDetailId) {
        return rfqDetailRepository.findById(rfqDetailId).orElseThrow(() -> new RuntimeException("RfqDetail not found"));
    }
    @Transactional
    @Override
    public void editRfqDetail(Rfq rfq) {
        if (rfq.getId() == null) {
            throw new IllegalArgumentException("RFQ ID must not be null");
        }

        try {
            // Lấy rfq đã có
            Rfq existingRfq = rfqService.getRfqById(rfq.getId());
            if (existingRfq == null) {
                System.err.println(("RFQ not found with ID: " + rfq.getId()));
            }
            System.err.println("Existing RFQ Details size before update: " + existingRfq.getRfqDetails().size());

            // Lấy danh sách updatedDetails từ rfq
            List<RfqDetail> updatedDetails = rfq.getRfqDetails() != null ? new ArrayList<>(rfq.getRfqDetails()) : new ArrayList<>();
            if (updatedDetails != null) {
                // Tạo một bản đồ để theo dõi các RfqDetail hiện có theo ID
                Map<Long, RfqDetail> existingDetailMap = existingRfq.getRfqDetails().stream()
                        .collect(Collectors.toMap(RfqDetail::getId, Function.identity()));

                for (RfqDetail detail : updatedDetails) {
                    // Đảm bảo rfq không null trước khi lưu
                    if (detail.getRfq() == null) {
                        detail.setRfq(existingRfq); // Gán rfq cho detail mới hoặc đã chỉnh sửa
                    }

                    if (detail.getProduct() == null || detail.getBrand() == null || detail.getCate() == null) {
                        throw new IllegalArgumentException("Product, Brand, and Category must not be null for RFQ Detail");
                    }

                    if (detail.getId() != null) {
                        // Cập nhật rfqDetail hiện có
                        RfqDetail existingDetail = rfqDetailRepository.findById(detail.getId())
                                .orElseThrow(() -> new RuntimeException("RFQ Detail not found"));
                        existingDetail.setProduct(productService.getProductById(detail.getProduct().getId()));
                        existingDetail.setBrand(brandService.getBrandById(detail.getBrand().getId()));
                        existingDetail.setCate(categoryService.getCategoryById(detail.getCate().getId()));
                        existingDetail.setNoteColor(detail.getNoteColor());
                        existingDetail.setQuantity(detail.getQuantity());
                        existingDetail.setRfq(existingRfq); // Đảm bảo rfq không null
                        rfqDetailRepository.save(existingDetail);
                        // Loại bỏ detail này khỏi existingDetailMap để tránh xóa nhầm
                        existingDetailMap.remove(existingDetail.getId());
                    } else {
                        // Thêm rfqDetail mới, gán rfq
                        detail.setRfq(existingRfq); // Đảm bảo rfq không null
                        rfqDetailRepository.save(detail);
                        System.out.println("Adding new detail: " + detail.getProduct().getName());
                    }
                }

                // Xóa các rfqDetails không còn trong updatedDetails (orphans)
                for (RfqDetail orphan : existingDetailMap.values()) {
                    rfqDetailRepository.delete(orphan);
                }

                // Cập nhật rfqDetails trong existingRfq bằng cách giữ nguyên tham chiếu
                existingRfq.getRfqDetails().clear(); // Xóa các phần tử cũ
                existingRfq.getRfqDetails().addAll(updatedDetails); // Thêm các phần tử mới

                // Lưu lại Rfq để cập nhật toàn bộ rfqDetails
                rfqService.saveRfq(existingRfq);
            }

            System.err.println("Existing RFQ Details size after update: " + existingRfq.getRfqDetails().size());
        } catch (Exception e) {
            System.err.println(("Error updating RFQ: " + e.getMessage()));
            throw e; // Ném lại exception để Spring xử lý
        }
    }

    }


