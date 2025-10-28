// 차트 인스턴스 관리
let currentChartInstances = {
    donut: null,
    bar: null
};

/**
 * 분석 결과 모달 열기
 * @param {number} resultId - 분석 결과 ID
 */
async function openAnalysisModal(resultId) {
    try {
        const response = await fetch(`/results/${resultId}`, {
            method: 'GET',
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('결과 데이터를 가져올 수 없습니다.');
        }

        const result = await response.json();
        displayAnalysisModal(result);

    } catch (error) {
        console.error('모달 열기 오류:', error);
        alert('분석 결과를 불러올 수 없습니다.');
    }
}

/**
 * 분석 결과 모달 표시
 * @param {Object} result - 분석 결과 데이터
 */
function displayAnalysisModal(result) {
    const modal = document.getElementById('analysisModal');

    // 분석일시
    document.getElementById('analysisDateText').textContent = `분석일시: ${result.analysisDate}`;

    // 종합 결과
    const totalResultDiv = document.getElementById('modalTotalResult');
    totalResultDiv.innerHTML = `
        <div style="margin-top: 1rem;">
            <p><strong>판정:</strong> <span style="color: ${result.suitable === '적합' ? '#4caf50' : '#f44336'}; font-weight: bold;">${result.suitable}</span></p>
            <p><strong>총 불순물 비율:</strong> ${result.total}%</p>
            <p><strong>검출된 불순물 수:</strong> ${result.count}개</p>
        </div>
    `;

    // R-CNN 이미지
    const rcnnGrid = document.getElementById('modalRCNNGrid');
    rcnnGrid.innerHTML = `
        <div class="modal-grid-item">
            <img src="${result.origImage}" class="modal-item-img" alt="원본 이미지">
            <p class="modal-item-label">&lt;원본 이미지&gt;</p>
        </div>
        <div class="modal-grid-item">
            <div class="modal-item-img" style="background-color: #f0f0f0;"></div>
        </div>
        <div class="modal-grid-item">
            <img src="${result.rcnnResult}" class="modal-item-img" alt="결과 이미지">
            <p class="modal-item-label">&lt;결과 이미지&gt;</p>
        </div>
    `;

    // OpenCV 이미지
    const opencvGrid = document.getElementById('modalOpenCVGrid');
    opencvGrid.innerHTML = `
        <div class="modal-grid-item">
            <img src="${result.origImage}" class="modal-item-img" alt="원본 이미지">
            <p class="modal-item-label">&lt;원본 이미지&gt;</p>
        </div>
        <div class="modal-grid-item">
            <img src="${result.opencvPro}" class="modal-item-img" alt="과정 이미지">
            <p class="modal-item-label">&lt;과정 이미지&gt;</p>
        </div>
        <div class="modal-grid-item">
            <img src="${result.opencvResult}" class="modal-item-img" alt="결과 이미지">
            <p class="modal-item-label">&lt;결과 이미지&gt;</p>
        </div>
    `;

    // PCA 및 바 차트 부분
    const pcaAndBar = document.getElementById('modalPCAAndBar');
    pcaAndBar.innerHTML = `
        <div class="bottom-grid-item">
            <div class="bottom-grid-title"><h2>PCA 분석</h2></div>
            <img src="${result.pca}" class="modal-item-img" alt="PCA 분석">
        </div>
        <div class="bottom-grid-item"></div>
        <div class="bottom-grid-item">
            <h4>불순물 검출량 비교</h4>
            <div class="modal-bar-container"><canvas id="modalBarChart"></canvas></div>
        </div>
    `;

    // 기존 차트 제거
    destroyModalCharts();

    // 도넛 차트 생성
    createDonutChart(result);

    // 바 차트 생성
    createBarChart(result);

    // 모달 표시
    modal.style.display = 'block';
}

/**
 * 도넛 차트 생성
 * @param {Object} result - 분석 결과 데이터
 */
function createDonutChart(result) {
    const totalImpurities = result.vinyl + result.plastic + result.wood;
    const ratioVinyl = totalImpurities > 0 ? (result.vinyl / totalImpurities * 100) : 0;
    const ratioPlastic = totalImpurities > 0 ? (result.plastic / totalImpurities * 100) : 0;
    const ratioWood = totalImpurities > 0 ? (result.wood / totalImpurities * 100) : 0;

    const donutCtx = document.getElementById("modalDonutChart").getContext('2d');
    currentChartInstances.donut = new Chart(donutCtx, {
        type: 'doughnut',
        data: {
            labels: ["폐비닐", "폐플라스틱", "폐목재"],
            datasets: [{
                data: [ratioVinyl, ratioPlastic, ratioWood],
                backgroundColor: ['#3b4d08', '#303e51', '#be563d']
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { 
                    display: true, 
                    position: 'bottom' 
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            return context.label + ': ' + context.parsed.toFixed(1) + '%';
                        }
                    }
                }
            }
        }
    });
}

/**
 * 바 차트 생성
 * @param {Object} result - 분석 결과 데이터
 */
function createBarChart(result) {
    const barCtx = document.getElementById("modalBarChart").getContext('2d');
    currentChartInstances.bar = new Chart(barCtx, {
        type: 'bar',
        data: {
            labels: ["폐비닐", "폐플라스틱", "폐목재"],
            datasets: [
                {
                    label: "전체 이미지 불순물 평균",
                    data: [result.avgVinyl || 0, result.avgPlastic || 0, result.avgWood || 0],
                    backgroundColor: '#f1512e'
                },
                {
                    label: "해당 이미지",
                    data: [result.vinyl, result.plastic, result.wood],
                    backgroundColor: 'black'
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { 
                    display: true,
                    position: 'top'
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: function(value) {
                            return value + '%';
                        }
                    }
                }
            }
        }
    });
}

/**
 * 모달 차트 제거 (메모리 누수 방지)
 */
function destroyModalCharts() {
    if (currentChartInstances.donut) {
        currentChartInstances.donut.destroy();
        currentChartInstances.donut = null;
    }
    if (currentChartInstances.bar) {
        currentChartInstances.bar.destroy();
        currentChartInstances.bar = null;
    }
}

/**
 * 모달 닫기
 */
function closeAnalysisModal() {
    const modal = document.getElementById('analysisModal');
    modal.style.display = 'none';
    destroyModalCharts();
}

/**
 * 모달 이벤트 리스너 초기화
 */
function initModalEventListeners() {
    // 닫기 버튼
    const closeBtn = document.getElementById('analysisModalClose');
    if (closeBtn) {
        closeBtn.addEventListener('click', closeAnalysisModal);
    }

    // 배경 클릭 시 닫기
    window.addEventListener('click', (e) => {
        const modal = document.getElementById('analysisModal');
        if (e.target === modal) {
            closeAnalysisModal();
        }
    });

    // 리포트 다운로드 버튼
    const downloadBtn = document.getElementById('downloadReportButton');
    if (downloadBtn) {
        downloadBtn.addEventListener('click', downloadReport);
    }
}

/**
 * 리포트 다운로드 (추후 구현)
 */
function downloadReport() {
    alert('리포트 다운로드 기능은 준비 중입니다.');
    // TODO: 실제 다운로드 로직 구현
}

// DOM 로드 시 이벤트 리스너 초기화
document.addEventListener('DOMContentLoaded', () => {
    initModalEventListeners();
});