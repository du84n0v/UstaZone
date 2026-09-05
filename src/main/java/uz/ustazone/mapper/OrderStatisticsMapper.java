package uz.ustazone.mapper;

public interface OrderStatisticsMapper {
    Long getTotalCount();
    Long getActiveCount();
    Long getCompletedCount();
}