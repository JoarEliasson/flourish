import * as echarts from './echarts/echarts.min.js';
import './echarts/echarts-liquidfill.min.js';

class MyLiquidGauge extends HTMLElement {
    constructor() {
        super();
        this._chart = null;
        this._option = null;
    }

    connectedCallback() {
        // Basic size or default style
        this.style.display = "block";
        this.style.width = this.style.width || "200px";
        this.style.height = this.style.height || "200px";

        // Initialize ECharts
        this._chart = echarts.init(this);

        // Basic liquidfill series
        this._option = {
            series: [{
                type: 'liquidFill',
                radius: '80%',
                data: [0.5], // default: 50% fill
                // color, wave animation speed, etc.
                backgroundStyle: {
                    color: '#eee'
                }
            }]
        };

        this._chart.setOption(this._option);
    }

    set value(newVal) {
        if (!this._chart) return;
        let val = parseFloat(newVal);
        if (Number.isNaN(val)) val = 0.5;
        val = Math.min(Math.max(val, 0), 1);
        this._option.series[0].data = [val];
        this._chart.setOption(this._option);
    }
}

customElements.define('my-liquid-gauge', MyLiquidGauge);
