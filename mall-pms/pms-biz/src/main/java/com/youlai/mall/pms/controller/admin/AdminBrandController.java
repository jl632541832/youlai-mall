package com.youlai.mall.pms.controller.admin;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.common.constant.GlobalConstants;
import com.youlai.common.enums.QueryModeEnum;
import com.youlai.common.result.Result;
import com.youlai.common.result.ResultCode;
import com.youlai.mall.pms.pojo.PmsBrand;
import com.youlai.mall.pms.service.IPmsBrandService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Api(tags = "品牌接口")
@RestController
@RequestMapping("/brands")
@Slf4j
@AllArgsConstructor
public class AdminBrandController {

    private IPmsBrandService iPmsBrandService;

    @ApiOperation(value = "列表分页", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "queryMode", paramType = "query", dataType = "QueryModeEnum"),
            @ApiImplicitParam(name = "page", value = "页码", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "limit", value = "每页数量", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "name", value = "品牌名称", paramType = "query", dataType = "String")
    })
    @GetMapping
    public Result list(String queryMode, Integer page, Integer limit, String name) {
        QueryModeEnum queryModeEnum = QueryModeEnum.getValue(queryMode);
        LambdaQueryWrapper<PmsBrand> queryWrapper = new LambdaQueryWrapper<>();
        switch (queryModeEnum) {
            case PAGE:
                queryWrapper.like(StrUtil.isNotBlank(name), PmsBrand::getName, name);
                Page<PmsBrand> result = iPmsBrandService.page(new Page<>(page, limit), queryWrapper);
                return Result.success(result.getRecords(), result.getTotal());
            case LIST:
                queryWrapper.eq(PmsBrand::getStatus, GlobalConstants.STATUS_NORMAL_VALUE)
                        .select(PmsBrand::getId, PmsBrand::getName);
                List<PmsBrand> list = iPmsBrandService.list(queryWrapper);
                return Result.success(list);
            default:
                return Result.failed(ResultCode.QUERY_MODE_IS_NULL);
        }
    }

    @ApiOperation(value = "品牌详情", httpMethod = "GET")
    @ApiImplicitParam(name = "id", value = "品牌id", required = true, paramType = "path", dataType = "Long")
    @GetMapping("/{id}")
    public Result detail(@PathVariable Integer id) {
        PmsBrand brand = iPmsBrandService.getById(id);
        return Result.success(brand);
    }

    @ApiOperation(value = "新增品牌", httpMethod = "POST")
    @ApiImplicitParam(name = "brand", value = "实体JSON对象", required = true, paramType = "body", dataType = "PmsBrand")
    @PostMapping
    public Result add(@RequestBody PmsBrand brand) {
        boolean status = iPmsBrandService.save(brand);
        return Result.judge(status);
    }

    @ApiOperation(value = "修改品牌", httpMethod = "PUT")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "品牌id", required = true, paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "brand", value = "实体JSON对象", required = true, paramType = "body", dataType = "PmsBrand")
    })
    @PutMapping(value = "/{id}")
    public Result update(
            @PathVariable Integer id,
            @RequestBody PmsBrand brand) {
        boolean status = iPmsBrandService.updateById(brand);
        return Result.judge(status);
    }

    @ApiOperation(value = "删除品牌", httpMethod = "DELETE")
    @ApiImplicitParam(name = "ids", value = "id集合", required = true, dataType = "String")
    @DeleteMapping("/{ids}")
    public Result delete(@PathVariable("ids") String ids) {
        boolean status = iPmsBrandService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.judge(status);
    }

    @ApiOperation(value = "修改品牌(部分更新)", httpMethod = "PATCH")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户id", required = true, paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "brand", value = "实体JSON对象", required = true, paramType = "body", dataType = "PmsBrand")
    })
    @PatchMapping(value = "/{id}")
    public Result patch(@PathVariable Integer id, @RequestBody PmsBrand brand) {
        LambdaUpdateWrapper<PmsBrand> luw = new LambdaUpdateWrapper<PmsBrand>().eq(PmsBrand::getId, id);
        if (brand.getStatus() != null) { // 状态更新
            luw.set(PmsBrand::getStatus, brand.getStatus());
        }
        boolean update = iPmsBrandService.update(luw);
        return Result.success(update);
    }
}
