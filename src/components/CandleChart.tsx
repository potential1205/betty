import React, { useRef, useEffect, useState } from 'react';
import {
    createChart,
    ColorType,
    CandlestickData,
    Time
} from 'lightweight-charts';

type Props = {
  dailyData: CandlestickData[];
  hourlyData: CandlestickData[];
  onPeriodChange?: (period: '1D' | '1H') => void;
  initialPeriod?: '1D' | '1H';
};

const CandleChart: React.FC<Props> = ({ dailyData, hourlyData, onPeriodChange, initialPeriod = '1D' }) => {
    const chartContainerRef = useRef<HTMLDivElement>(null);
    const tooltipRef = useRef<HTMLDivElement>(null);
    const [period, setPeriod] = useState<'1D' | '1H'>(initialPeriod);

    // 탭이 변경될 때 콜백 호출
    const handlePeriodChange = (newPeriod: '1D' | '1H') => {
        setPeriod(newPeriod);
        if (onPeriodChange) {
            onPeriodChange(newPeriod);
        }
    };

    useEffect(() => {
        // initialPeriod가 변경되면 period 상태 업데이트
        setPeriod(initialPeriod);
    }, [initialPeriod]);

    useEffect(() => {
        if (!chartContainerRef.current) return;

        const handleResize = () => {
            if (chartContainerRef.current) {
                chart.applyOptions({ width: chartContainerRef.current.clientWidth });
            }
        };

        const chart = createChart(chartContainerRef.current, {
            width: chartContainerRef.current.clientWidth,
            height: 224,
            layout: {
                background: { color: '#ffffff' },
                textColor: '#333',
            },
            grid: {
                vertLines: { color: '#f0f0f0' },
                horzLines: { color: '#f0f0f0' },
            },
            timeScale: {
                timeVisible: true,
                secondsVisible: false,
            },
        });

        const series = chart.addCandlestickSeries({
            upColor: '#FF2D55',
            downColor: '#007AFF',
            borderUpColor: '#FF2D55',
            borderDownColor:'#007AFF',
            wickUpColor:'#FF2D55',
            wickDownColor: '#007AFF',
        });

        const rawData = period === '1D' ? dailyData : hourlyData;
        const convertedData: CandlestickData[] = rawData.map((item) => {
            const timestamp = Math.floor(new Date(item.time as string).getTime() / 1000);
          
            return {
              time: timestamp as Time,
              open: item.open,
              high: item.high,
              low: item.low,
              close: item.close,
            };
        });
          
        series.setData(convertedData);

        chart.timeScale().fitContent();

        const tooltip = document.createElement('div');
        tooltip.className = 'custom-tooltip';
        tooltip.style.cssText = `
            position: absolute;
            display: none;
            padding: 4px 8px;
            background: rgba(0, 0, 0, 0.8);
            color: #fff;
            font-size: 12px;
            border-radius: 4px;
            pointer-events: none;
            z-index: 1000;
        `
        chartContainerRef.current.appendChild(tooltip);
        tooltipRef.current = tooltip;

        chart.subscribeCrosshairMove(param => {
            if (
                !param.time ||
                !param.point ||
                typeof param.point.x !== 'number' ||
                typeof param.point.y !== 'number' ||
                !tooltipRef.current
            ) {
                if (tooltipRef.current) tooltipRef.current.style.display = 'none';
                return;
            }

            const unix = (param.time as any) * 1000;
            const kstDate = new Date(unix + 9 * 60 * 60 * 1000);
            const formatted = `${kstDate.getFullYear()}-${('0' + (kstDate.getMonth() + 1)).slice(-2)}-${('0' + kstDate.getDate()).slice(-2)}`;

            tooltipRef.current.innerText = formatted;
            tooltipRef.current.style.left = param.point.x + 10 + 'px';
            tooltipRef.current.style.top = param.point.y + 10 + 'px';
            tooltipRef.current.style.display = 'block';
        });

        const resizeObserver = new ResizeObserver(() => {
            if (chartContainerRef.current) {
                handleResize();
            }
        });

        if (chartContainerRef.current) {
            resizeObserver.observe(chartContainerRef.current);
        }

        return () => {
            chart.remove();
            resizeObserver.disconnect();
            if (tooltipRef.current) {
                tooltipRef.current.remove();
            }
        };
    }, [dailyData, hourlyData, period]);
    
    return (
        <div style={{ position: 'relative', width: '100%' }}>
            <div className="flex justify-end pr-2 pb-2">
                <div className="bg-gray-200 p-0.5 rounded-lg flex items-center">
                    <button
                        className={`text-xs font-['Giants-Bold'] px-3 py-1 rounded-md transition-all ${
                            period === '1D' 
                            ? 'bg-blue-500 text-white shadow-sm' 
                            : 'text-gray-500 hover:bg-gray-300'
                        }`}
                        onClick={() => handlePeriodChange('1D')}
                    >
                        1일
                    </button>
                    <button
                        className={`text-xs font-['Giants-Bold'] px-3 py-1 rounded-md ml-1 transition-all ${
                            period === '1H' 
                            ? 'bg-blue-500 text-white shadow-sm' 
                            : 'text-gray-500 hover:bg-gray-300'
                        }`}
                        onClick={() => handlePeriodChange('1H')}
                    >
                        1시간
                    </button>
                </div>
            </div>
            <div
                ref={chartContainerRef}
                style={{ width: '100%', height: '224px', position: 'relative' }}
            />
        </div>
    );
};

export default CandleChart;
