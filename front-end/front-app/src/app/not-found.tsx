import Link from 'next/link'
 
export default function NotFound() {
  return (
    <div>
      <h2>Página no encontrada</h2>
      <p>Lo sentimos, no se pudo encontrar el recurso solicitado.</p>
      <Link href="/">Volver a inicio</Link>
    </div>
  )
}