import * as React from 'react'
import { useRef, useEffect, useState } from 'react'

export default function Tablica() {
    const ref = useRef<HTMLCanvasElement | null>(null)
    const [rysuje, setRysuje] = useState(false)
    const [kolor, setKolor] = useState('#ffffff')
    const [grubosc, setGrubosc] = useState(3)

    useEffect(() => {
        const canvas = ref.current!
        const ctx = canvas.getContext('2d')!

        function dopasujDPI() {
            const dpr = window.devicePixelRatio || 1
            const rect = canvas.getBoundingClientRect()
            canvas.width = Math.floor(rect.width * dpr)
            canvas.height = Math.floor(rect.height * dpr)
            ctx.scale(dpr, dpr)
            ctx.lineJoin = 'round'
            ctx.lineCap = 'round'
        }

        dopasujDPI()
        const ro = new ResizeObserver(dopasujDPI)
        ro.observe(canvas)
        return () => ro.disconnect()
    }, [])

    function pozycja(e: PointerEvent | React.PointerEvent) {
        const canvas = ref.current!
        const rect = canvas.getBoundingClientRect()
        return { x: e.clientX - rect.left, y: e.clientY - rect.top }
    }

    function start(e: React.PointerEvent<HTMLCanvasElement>) {
        const ctx = ref.current!.getContext('2d')!
        const p = pozycja(e)
        ctx.beginPath()
        ctx.moveTo(p.x, p.y)
        ctx.strokeStyle = kolor
        ctx.lineWidth = grubosc
        setRysuje(true)
    }
    function move(e: React.PointerEvent<HTMLCanvasElement>) {
        if (!rysuje) return
        const ctx = ref.current!.getContext('2d')!
        const p = pozycja(e)
        ctx.lineTo(p.x, p.y)
        ctx.stroke()
    }
    function stop() { setRysuje(false) }

    function wyczysc() {
        const canvas = ref.current!
        const ctx = canvas.getContext('2d')!
        ctx.clearRect(0, 0, canvas.width, canvas.height)
    }

    return (
        <div className="panel">
            <h2>Tablica – rysowanie</h2>

            <div className="przyciski" style={{marginBottom: 12}}>
                <label style={{display:'flex', alignItems:'center', gap:8}}>
                    Kolor
                    <input type="color" value={kolor} onChange={e => setKolor(e.target.value)} />
                </label>
                <label style={{display:'flex', alignItems:'center', gap:8}}>
                    Grubość
                    <input type="range" min={1} max={32} value={grubosc} onChange={e => setGrubosc(Number(e.target.value))} />
                </label>
                <button type="button" onClick={wyczysc}>Wyczyść</button>
            </div>

            <div style={{border:'1px solid var(--ramka)', borderRadius:12, height: '60vh', overflow:'hidden'}}>
                <canvas
                    ref={ref}
                    style={{width:'100%', height:'100%', touchAction:'none', background:'#0f1016'}}
                    onPointerDown={start}
                    onPointerMove={move}
                    onPointerUp={stop}
                    onPointerLeave={stop}
                />
            </div>
        </div>
    )
}
