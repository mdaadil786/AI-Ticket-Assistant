import { useEffect, useMemo, useRef } from 'react'
import { GitBranch } from 'lucide-react'
import { useAssistantStore } from '../stores/assistantStore'

const labels: Record<string, string> = {
  workflow_started: 'Workflow started',
  agent_started: 'Agent started',
  agent_completed: 'Agent completed',
  tool_call: 'Tool requested',
  tool_result: 'Tool result',
  knowledge_sources: 'RAG sources',
  order_confirmation_required: 'Order confirmation',
  final: 'Answer stream',
  trace: 'Trace summary',
  error: 'Error'
}

export function TraceTimeline() {
  const allEvents = useAssistantStore((state) => state.events)
  const events = useMemo(() => allEvents.filter((event) => event.type !== 'final'), [allEvents])
  const listRef = useRef<HTMLDivElement | null>(null)

  useEffect(() => {
    listRef.current?.scrollTo({ top: listRef.current.scrollHeight, behavior: 'smooth' })
  }, [events.length])

  return (
    <section className="glass rounded-[2rem] p-5">
      <div className="mb-4 flex items-center justify-between gap-2">
        <div className="flex items-center gap-2">
          <GitBranch size={18} className="text-slate-300" />
          <h3 className="font-semibold text-white">Workflow Trace</h3>
        </div>
        {events.length > 0 && <span className="rounded-full bg-white/5 px-2 py-1 text-xs text-slate-400">{events.length} events</span>}
      </div>
      <div ref={listRef} className="max-h-80 space-y-3 overflow-auto pr-1">
        {events.length === 0 && <p className="text-sm text-slate-500">SSE events are appended here as they arrive.</p>}
        {events.map((event, index) => (
          <div key={`${event.type}-${event.timestamp}-${index}`} className="animate-[traceIn_260ms_ease-out] border-l border-white/10 pl-3">
            <div className="flex items-center justify-between gap-3">
              <p className="text-xs uppercase tracking-wider text-cyan-200">{labels[event.type] ?? event.type}</p>
              <span className="text-[10px] text-slate-500">#{index + 1}</span>
            </div>
            <p className="text-sm text-slate-300">{event.message}</p>
          </div>
        ))}
      </div>
    </section>
  )
}
