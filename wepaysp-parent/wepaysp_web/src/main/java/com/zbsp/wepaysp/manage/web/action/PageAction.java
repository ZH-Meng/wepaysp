package com.zbsp.wepaysp.manage.web.action;

/**
 * 需要使用分页的Action应继承此类
 * 
 * @author Magic
 */
public abstract class PageAction extends BaseAction {

    private static final long serialVersionUID = -6900242847620137144L;
    
    protected static int defaultSmallPageSize = 20;
    protected static int defaultMiddlePageSize = 50;
    protected static int defaultLargePageSize = 100;
    protected int rowCount = 0; // 总记录数
    protected int pageCount = 0; // 总页数
    protected int currPage = 1; // 当前页
    protected int pageRows = 15; // 每页行数
    protected String goPage = "1"; // 指定页数

    protected String disabledFirst; // 第一页是否可用
    protected String disabledBack; // 上一页是否可用
    protected String disabledNext; // 下一页是否可用
    protected String disabledLast; // 是否最后一页
    protected String disabledGoPage; // 跳转是否可见
    protected String disabledSetPageRows; // 设定行数是否可见

    /**
     * 初始化分页参数
     */
    public void initPageData() {
        rowCount = 0;
        pageCount = 0;
        currPage = 1;
        goPage = "1";

        disabledFirst = "true";
        disabledBack = "true";
        disabledNext = "true";
        disabledLast = "true";
        disabledGoPage = "true";
        disabledSetPageRows = "true";
    }

    /**
     * 初始化分页参数
     * 
     * @param pageRows 每页行数
     */
    public void initPageData(int pageRows) {
        rowCount = 0;
        pageCount = 0;
        currPage = 1;
        goPage = "1";

        if (pageRows > 0) {
            this.pageRows = pageRows;
        }

        disabledFirst = "true";
        disabledBack = "true";
        disabledNext = "true";
        disabledLast = "true";
        disabledGoPage = "true";
        disabledSetPageRows = "true";
    }

    /**
     * 查询数据,子类必须覆盖该方法
     * 
     * @param start 起始索引
     * @param size 每次查询数量
     * @return 
     */
    protected abstract String query(int start, int size);

    /**
     * 查询结果后，初始化分页的数据
     */
    public void initPage() {
        pageCount = rowCount / pageRows + (rowCount % pageRows == 0 ? 0 : 1);
        if (rowCount == 0) {
            currPage = 1;
            goPage = "1";
        }

        initDisable();
    }

    /**
     * 初始化page控件的可见性
     */
    private void initDisable() {
        if (rowCount == 0) {
            disabledFirst = "true";
            disabledBack = "true";
            disabledNext = "true";
            disabledLast = "true";

            disabledGoPage = "true";
            disabledSetPageRows = "true";
        } else {
            disabledGoPage = "false";
            disabledSetPageRows = "false";

            // 如果是第一页,第一页和上一页不显示
            if (currPage == 1) {
                disabledFirst = "true";
                disabledBack = "true";
            } else {
                disabledFirst = "false";
                disabledBack = "false";
            }

            // 如果是最后一页,最后一页和下一页不显示
            if (currPage == pageCount) {
                disabledNext = "true";
                disabledLast = "true";
            } else {
                disabledNext = "false";
                disabledLast = "false";
            }
        }
    }

    /**
     * 转到指定页
     */
    private String goQuery(int startPage) {
        currPage = startPage;
        goPage = String.valueOf(currPage);
        String ret = query(pageRows * (startPage - 1), pageRows);
        initPage();

        if (pageCount < startPage && pageCount > 0) {
            startPage = pageCount;
            ret = goQuery(startPage);
        }

        return ret;
    }

    /**
     * 到当前页
     * 
     * @return
     */
    public String goCurrent() {
        return goQuery(currPage);
    }

    /**
     * 第一页
     * 
     * @return
     */
    public String goFirst() {
        currPage = 1;
        return goQuery(currPage);
    }

    /**
     * 最后一页
     * 
     * @return
     */
    public String goLast() {
        return goQuery(pageCount);
    }

    /**
     * 上一页
     * 
     * @return
     */
    public String goBack() {
        if (currPage == 1) {
            return goFirst();
        }
        
        return goQuery(currPage - 1);
    }

    /**
     * 下一页
     * 
     * @return
     */
    public String goNext() {
        if (currPage == pageCount) {
            return goLast();
        }

        return goQuery(currPage + 1);
    }

    /**
     * 跳到指定页,指定页=getPage();
     * 
     * @return
     */
    public String reGoPage() {
        if (pageCount == 0) {
            pageCount = 1;
        }
        if (Integer.parseInt(goPage) > pageCount) {
            goPage = String.valueOf(pageCount);
        }

        currPage = Integer.parseInt(goPage);
        return goQuery(currPage);
    }

    /**
     * 设定每页行数
     * 
     * @return
     */
    public String reSetPageRows() {
        return goFirst();
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getCurrPage() {
        return currPage;
    }

    public void setCurrPage(int currPage) {
        this.currPage = currPage;
    }

    public int getPageRows() {
        return pageRows;
    }

    public void setPageRows(int pageRows) {
        if (pageRows <= 0) {
            pageRows = 12;
        }
        
        this.pageRows = pageRows;
    }

    public String getGoPage() {
        return goPage;
    }

    public void setGoPage(String goPage) {
        if (!validateFormData(goPage)) {
            this.goPage = String.valueOf(currPage);
        } else {
            if (Integer.parseInt(goPage) <= 0) {
                goPage = "1";
            }

            this.goPage = goPage;
        }
    }

    public String getDisabledFirst() {
        return disabledFirst;
    }

    public void setDisabledFirst(String disabledFirst) {
        this.disabledFirst = disabledFirst;
    }

    public String getDisabledBack() {
        return disabledBack;
    }

    public void setDisabledBack(String disabledBack) {
        this.disabledBack = disabledBack;
    }

    public String getDisabledNext() {
        return disabledNext;
    }

    public void setDisabledNext(String disabledNext) {
        this.disabledNext = disabledNext;
    }

    public String getDisabledLast() {
        return disabledLast;
    }

    public void setDisabledLast(String disabledLast) {
        this.disabledLast = disabledLast;
    }

    public String getDisabledGoPage() {
        return disabledGoPage;
    }

    public void setDisabledGoPage(String disabledGoPage) {
        this.disabledGoPage = disabledGoPage;
    }

    public String getDisabledSetPageRows() {
        return disabledSetPageRows;
    }

    public void setDisabledSetPageRows(String disabledSetPageRows) {
        this.disabledSetPageRows = disabledSetPageRows;
    }

    private boolean validateFormData(String goPage) {
        if (goPage == null || !goPage.matches("\\d+")) {
            setAlertMessage("跳转页应输入正整数");
            return false;
        }

        return true;
    }
}
