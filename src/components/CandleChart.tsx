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
};

const CandleChart: React.FC<Props> = ({ dailyData, hourlyData }) => {
    const chartContainerRef = useRef<HTMLDivElement>(null);
    const tooltipRef = useRef<HTMLDivElement>(null);
    const [period, setPeriod] = useState<'1D' | '1H'>('1D');

    useEffect(() => {
        if (!chartContainerRef.current) return;

        const chart = createChart(chartContainerRef.current, {
            width: chartContainerRef.current.clientWidth,
            height: 224,
            layout: {
                background: { type: ColorType.Solid, color: '#ffffff' },
                textColor: '#000',
                fontSize: 12,
                fontFamily: "'Pretendard', sans-serif",
            },
            grid: {
                vertLines: { color: '#f0f0f0', style: 0 },
                horzLines: { color: '#f0f0f0', style: 0 },
            },
            timeScale: {
                timeVisible: true,
                secondsVisible: false,
                borderColor: '#D1D4DC',
                rightOffset: 5,
                barSpacing: 5,
                tickMarkFormatter: (time: Time) => {
                    const date = new Date((time as any) * 1000);
                    const year = date.getFullYear();
                    const month = ('0' + (date.getMonth() + 1)).slice(-2);
                    const day = ('0' + date.getDate()).slice(-2);
                    return `${year}-${month}-${day}`;
                },
            },
            localization: {
                locale: 'ko-KR',
                dateFormat: 'yyyy-MM-dd',
            },
            rightPriceScale: {
                borderColor: '#D1D4DC',
                scaleMargins: {
                    top: 0.2,
                    bottom: 0.15,
                },
            },
            crosshair: {
                mode: 0,
                vertLine: {
                    width: 1,
                    color: '#9B9B9B',
                    style: 1,
                    visible: true,
                    labelVisible: true,
                },
                horzLine: {
                    color: '#9B9B9B',
                    style: 1,
                    visible: true,
                    labelVisible: true,
                },
            },
            handleScroll: {
                pressedMouseMove: true,
                mouseWheel: true,
            },
            handleScale: {
                mouseWheel: true,
                pinch: true,
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
            chart.applyOptions({ width: chartContainerRef.current!.clientWidth });
        });

        resizeObserver.observe(chartContainerRef.current);

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
            <div className="flex justify-end pr-2 pb-1">
                <button
                    className={`text-xs font-bold mr-2 ${period === '1D' ? 'text-blue-500' : 'text-gray-400'}`}
                    onClick={() => setPeriod('1D')}
                >
                    1일
                </button>
                <button
                    className={`text-xs font-bold ${period === '1H' ? 'text-blue-500' : 'text-gray-400'}`}
                    onClick={() => setPeriod('1H')}
                >
                    1시간
                </button>
            </div>
            <div
                ref={chartContainerRef}
                style={{ width: '100%', height: '224px', position: 'relative' }}
            />
        </div>
    );
};

export default CandleChart;
